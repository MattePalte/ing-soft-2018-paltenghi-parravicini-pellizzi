package project.ing.soft.cli;

import project.ing.soft.IExceptionalProcedure;
import project.ing.soft.Settings;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.gamemodel.events.*;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class ClientViewCLI extends UnicastRemoteObject
        implements IView, IEventHandler, Serializable, IToolCardParametersAcquirer {

    private final String                    ownerNameOfTheView;
    private String                          personalToken;
    private boolean                         gameOnGoing;
    private IGameModel                      localCopyOfTheStatus;
    private Timestamp                       expectedEndTurn;

    private transient IController           controller;

    private transient Logger                log;
    private transient Console               out;
    private transient NonBlockingScanner    scanner;



    private final transient Map<String, IExceptionalProcedure> commands;

    private transient ExecutorService       threadPool;
    private final transient Queue<Event>    eventsReceived;

    private transient Future                userThread;
    private transient Future                eventHandler;


    private static final Integer PROGRESS_BAR_LENGTH = 40;
    private transient Timer timer;
    private transient TimerTask progressBarTask;
    public ClientViewCLI(String ownerNameOfTheView, IController controller) throws RemoteException {
        this(ownerNameOfTheView);
        this.controller           = controller;
    }

    public ClientViewCLI(String ownerNameOfTheView) throws RemoteException {
        super();

        this.ownerNameOfTheView   = ownerNameOfTheView;
        this.log                  = Logger.getLogger(ownerNameOfTheView);
        this.log.setLevel(Level.OFF);
        this.out                  = new Console(System.out);
        this.scanner              = new NonBlockingScanner(System.in);
        this.gameOnGoing          = true;
        this.localCopyOfTheStatus = null;

        this.expectedEndTurn      = null;
        this.timer                = new Timer();
        this.commands             = new LinkedHashMap<>();
        this.commands.put("Placing a die"     ,           this::placeDieOperation);
        this.commands.put("Playing a ToolCard" ,           this::playAToolCardOperation);
        this.commands.put("Showing the entire situation",  this::displayEntireGameBoard);
        this.commands.put("Showing my situation",          this::displayMySituation);
        this.commands.put("Ending turn",                   this::endTurnOperation);
        this.commands.put("Disconnecting from game",       this::stop);
        this.commands.put("Enabling loggin features", ()-> log.setLevel(log.getLevel() == Level.OFF ? Level.ALL : Level.OFF));

        this.eventsReceived       = new LinkedList<>();
        this.threadPool           = Executors.newCachedThreadPool();

        eventHandler = threadPool.submit( this::eventHandlingFunction);
    }


    private boolean eventHandlingFunction() throws InterruptedException {
        Event toRespond;

        while(gameOnGoing) {

            synchronized (eventsReceived) {
                while (eventsReceived.isEmpty())
                    eventsReceived.wait();
                toRespond = eventsReceived.remove();
            }

            if (toRespond != null){
                toRespond.accept(this);
            }
        }
        return true;
    }

    @Override
    public void attachController(IController aController){
        this.controller = aController;
    }

    //region event handling
    @Override
    public void update(Event aEvent) {
        log.log(Level.INFO,  "{0} received an event : {1}" ,new Object[]{ ownerNameOfTheView, aEvent});

        if (gameOnGoing) {
            synchronized (eventsReceived) {
                eventsReceived.add(aEvent);
                eventsReceived.notifyAll();
            }
        }
    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        out.println("Now it's others' turn");
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        out.println("Finished setup.. wait while the game start");
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();

        log.info("Model updated!");
        if (!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)) {
            out.println("It's the turn of " + localCopyOfTheStatus.getCurrentPlayer().getName() + ". Wait for yours.");
        }

    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        log.info("PatternCardEvent received");
        threadPool.submit( ()-> choosePatternCardOperation(event));
    }

    @Override
    public void respondTo(SetTokenEvent event) {
        log.log(Level.INFO,"Token received");
        log.log(Level.INFO,"Your token to ask reconnection is {0} " , event.getToken());
        Preferences pref = Preferences.userRoot().node(Settings.instance().getProperty("preferences.location"));
        pref.put(Settings.instance().getProperty("preferences.connection.token.location"), event.getToken());
        try {
            pref.flush();
        } catch (BackingStoreException e) {
            log.log(Level.INFO,"exception thrown while saving token" , e);
        }
        personalToken = event.getToken();
        out.println("Your token to ask reconnection is "+event.getToken());
        out.println("Connection established. Please, wait for the game to start");

    }

    @Override
    public void respondTo(PlayerReconnectedEvent event) {
        log.log(Level.INFO,  "{0}: player reconnected", event.getNickname() );
        out.println(Colour.RED.colourForeground(event.getNickname() + " reconnected to the game"));
    }

    @Override
    public void respondTo(PlayerDisconnectedEvent event) {
        log.log(Level.INFO, "{0}: player disconnected",event.getNickname());
        out.println(Colour.RED.colourForeground(event.getNickname() + " disconnected from the game"));
    }

    @Override
    public void respondTo(ToolcardActionRequestEvent event) {
        log.log(Level.INFO, "ToolCard action request");
        if(userThread !=null)
            userThread.cancel(true);
        userThread = threadPool.submit(()-> completeToolCardOperation(event));
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        log.log(Level.INFO, "Turn started event received");
        if(event.getEndTurnTimeStamp() != null) {
            if(progressBarTask != null)
                progressBarTask.cancel();
            expectedEndTurn = event.getEndTurnTimeStamp();
            if(Settings.instance().isDeploy()) {
                progressBarTask = new TimerTask() {
                    @Override
                    public void run() {
                        printProgressBar();
                    }
                };
                timer.schedule(progressBarTask, 2000, 2000);
            }
        }
        if(userThread != null ) {
            userThread.cancel(true);
        }
        userThread = threadPool.submit(this:: takeTurn);
    }

    //region operations
    private void endTurnOperation() throws Exception {
        controller.endTurn(ownerNameOfTheView);

        if(progressBarTask != null)
            progressBarTask.cancel();

        Thread.currentThread().interrupt();
    }

    private void choosePatternCardOperation(PatternCardDistributedEvent event){
        boolean done = false;
        WindowPatternCard aCard = null;
        int isFront = 0;

        do {
            try {
                out.println("This is your private objective: ");
                out.println(event.getMyPrivateObjective());
                aCard = (WindowPatternCard) chooseFrom(
                        new ArrayList<>(Arrays.asList(event.getOne(), event.getTwo()))
                );
                isFront = chooseIndexFrom(
                        new ArrayList<>(Arrays.asList(aCard.getFrontPattern(), aCard.getRearPattern()))
                );
                done = true;
            } catch (UserInterruptActionException ex) {
                out.println("The game can't start until you select a window pattern");
            } catch (Exception e) {
                displayError(e);
            }
        } while (!done);

        try {
            out.println("Wait for other players to choose their pattern card.");
            controller.choosePattern(ownerNameOfTheView, aCard, isFront == 1);
        } catch (Exception e) {
            displayError(e);
        }
    }

    private void playAToolCardOperation() throws Exception {
        out.println("Choose a ToolCard: ");
        ToolCard aToolCard = chooseFromToolCard(localCopyOfTheStatus.getToolCards());
        aToolCard.fill(this);
        controller.playToolCard(ownerNameOfTheView, aToolCard);
        Thread.currentThread().interrupt();
    }

    private void completeToolCardOperation(ToolcardActionRequestEvent event) {
        out.println("Server request other parameter to complete the ToolCard");
        boolean done = false;
        do {
            try {
                ToolCard aToolCard = event.getCard();
                aToolCard.fill(this);
                controller.playToolCard(ownerNameOfTheView, aToolCard);
                done = true;
            } catch (UserInterruptActionException ignored) {
                out.println("You can't leave in the middle of a ToolCard operation");
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }catch (Exception ex) {
                displayError(ex);
            }
        }while(!done);
    }

    private void placeDieOperation() throws Exception{
        displayMySituation();
        out.println("Select a die from DraftPool:");
        Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
        Coordinate placePosition = chooseDieCoordinate("Enter where you want to place your die");
        controller.placeDie(ownerNameOfTheView, chosenDie, placePosition.getRow(), placePosition.getCol());
        Thread.currentThread().interrupt();
    }

    private void takeTurn() {
        String cmd;


        do{
            out.clear();
            printProgressBar();
            displayMySituation();
            out.println("Take your turn " + localCopyOfTheStatus.getCurrentPlayer().getName());
            cmd = null;
            try {
                cmd = (String) chooseFrom(new ArrayList<>(commands.keySet()));
                commands.get(cmd).run();
            } catch (UserInterruptActionException e) {
                out.println( cmd != null ? "Operation aborted" : "If you do not want to perform any action, please end your turn.");
            }catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                displayError(e);
            }

        }while(!Thread.currentThread().isInterrupted());
    }

    private void printProgressBar() {
        if(expectedEndTurn == null)
            return;

        out.saveCursorPosition();
        out.setCursorPosition(1,1);
        float percentage = (float)(expectedEndTurn.getTime() - System.currentTimeMillis())/Settings.instance().getTurnTimeout();
        percentage = Float.max(percentage, 0);
        out.println( "Time left: "+String.format( String.format("[%%-%ds]", PROGRESS_BAR_LENGTH), new String(new char[(int) (PROGRESS_BAR_LENGTH * percentage)]).replace("\0", "=")));
        out.restoreCursorPosition();
    }

    //endregion
    @Override
    public void respondTo(MyTurnEndedEvent event){
        out.println("You had plenty of time and you didn't used.. the turn was ended by the server");
        userThread.cancel(true);
        if(progressBarTask != null)
            progressBarTask.cancel();
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        String winner;
        gameOnGoing = false;
        out.println("\nGame finished!");

        Map<String, String> pointsDescriptor = event.getPointsDescriptor();
        for(Player p : localCopyOfTheStatus.getPlayerList()){
            out.println(pointsDescriptor.get(p.getName()));
            // Create an empty line between players' descriptors
            out.println();
        }
        out.println("Final Rank:");
        for (Pair<Player, Integer> aPair : event.getRank()){
            out.println(aPair.getKey() + " => " + aPair.getValue());
            out.println();
        }
        List<Player> connectedPlayers = localCopyOfTheStatus.getPlayerList().stream().filter(Player::isConnected).collect(Collectors.toList());
        if(connectedPlayers.size() == 1)
            winner = connectedPlayers.get(0).getName();
        else
            winner = event.getRank().get(0).getKey().getName();
        out.println("\nThe winner is " + winner);
        stop();
    }

    //endregion

    //region helper functions

    private void displayError(Exception ex) {
        log.log(Level.SEVERE, "Error", ex);
        out.println("Error: "+ex.getMessage());
        out.println("Do you need stack trace? [y/n]");

        try {
            if(scanner.readLine().startsWith("y"))
                ex.printStackTrace(out);
        } catch (InterruptedException ignored) {
            out.println("Input operation interrupted");
            Thread.currentThread().interrupt();
        }
    }

    private void displayMySituation(){
        out.println("Turn: "+localCopyOfTheStatus.getRoundTracker().getCurrentRound());
        // Print only the situation of the current player, i.e. the owner of this view.
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                out.println(p);
            }
        }
        out.println("Favours: "+localCopyOfTheStatus.getFavours().get(localCopyOfTheStatus.getCurrentPlayer().getName()));
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
        out.println("RoundTracker dice left : "+ localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound());
    }

    private void displayEntireGameBoard(){
        out.println("Public objectives:");
        out.println(Card.drawNear(localCopyOfTheStatus.getPublicObjective()));
        out.println("ToolCards:");
        out.println(Card.drawNear(localCopyOfTheStatus.getToolCards()));

        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            out.println(p);
            out.println(p.getName()+" favours: "+localCopyOfTheStatus.getFavours().get(localCopyOfTheStatus.getCurrentPlayer().getName()));
        }
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
    }

    public void run() {
        out.println(ownerNameOfTheView + " started ");
        out.println("Waiting for enough players to start the match...");

    }
//endregion

    //region user input

    private int waitForUserInput( int upperBound) throws UserInterruptActionException, InterruptedException {
        int ret      = 0;
        boolean done = false;

        do{
            String input = scanner.readLine();
            try{
                ret = Integer.valueOf(input);
                done = ret >= 0 && ret <= upperBound;
            }
            catch( NumberFormatException ignored){
                //No action need to be performed here.
            }

            if(!done){
                if(input.startsWith("q"))
                    throw new UserInterruptActionException();
                out.println("You entered a value that does not fit into the correct interval. Enter q to interrupt the operation");

            }
        }while(!done);

        return ret;
    }

    private Coordinate chooseDieCoordinate (String caption) throws UserInterruptActionException, InterruptedException {
        out.println(caption);
        int row ;
        int col ;
        out.println("Row Index [0 - 3]");
        row = waitForUserInput( 3);
        out.println("Col Index [0 - 4]");
        col = waitForUserInput( 4);
        return new Coordinate(row, col);

    }

    private ToolCard chooseFromToolCard(List<ToolCard> objs) throws UserInterruptActionException, InterruptedException {
        out.println(Card.drawNear(IntStream.range(0,objs.size()).mapToObj(i ->
                        "["+i+"]\n"+
                        objs.get(i).toString()+"\n"+
                        "Cost: " + localCopyOfTheStatus.getToolCardCost().get(objs.get(i).getTitle()))
                .collect(Collectors.toList())));
        out.println(String.format("Enter a number between 0 and %d to select:", objs.size()-1));
        return objs.get(waitForUserInput(objs.size()-1));
    }

    private Object chooseFrom(List objs) throws UserInterruptActionException, InterruptedException {
        return objs.get(chooseIndexFrom(objs));
    }

    private int chooseIndexFrom(List objs) throws UserInterruptActionException, InterruptedException {

        out.println(String.format("Enter a number between 0 and %d to select:", objs.size()-1));
        for (int i = 0; i < objs.size() ; i++) {
            out.println(String.format("[%d] for %s", i, objs.get(i).toString()));
        }
        return waitForUserInput( objs.size()-1);

    }
//endregion

    //region parameters acquirer
    @Override
    public Die getDieFromDraft(String message) throws InterruptedException, UserInterruptActionException {
        out.println(message);
        return (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
    }

    @Override
    public Die getDieFromRound(String message) throws InterruptedException, UserInterruptActionException {
        out.println(message);
        return (Die) chooseFrom(localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound());
    }

    @Override
    public Coordinate getCoordinate(String message) throws InterruptedException, UserInterruptActionException {
        return chooseDieCoordinate(message);
    }

    @Override
    public int getValue(String message, Integer... values) throws InterruptedException, UserInterruptActionException {
        out.println(message);
        return (Integer) chooseFrom( Arrays.asList(values) );
    }

    /**
     *
     * @param message the message to be printed
     * @return true if user choose "yes", false otherwise
     * @throws InterruptedException if timeout expires while user is making his choice
     * @throws UserInterruptActionException if user abort operation
     */
    @Override
    public boolean getAnswer(String message) throws InterruptedException, UserInterruptActionException {
        out.println(message);
        return ((String) chooseFrom(Arrays.asList("yes", "no"))).toLowerCase().startsWith("y");
    }
    //endregion

    private void stop(){
        if(userThread != null)
            userThread.cancel(true);
        eventHandler.cancel(true);
        threadPool.shutdown();
        this.controller = null;

        if(localCopyOfTheStatus.getStatus() != IGameModel.GAME_MANAGER_STATUS.ENDED)
            out.println("To reconnect you can use this token: " + personalToken);
        //the following line must remain. In other case no unreferenced() get called!
        System.gc();
        System.exit(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClientViewCLI that = (ClientViewCLI) o;
        return Objects.equals(ownerNameOfTheView, that.ownerNameOfTheView) &&
                Objects.equals(localCopyOfTheStatus, that.localCopyOfTheStatus) &&
                Objects.equals(controller, that.controller) &&
                Objects.equals(out, that.out) &&
                Objects.equals(scanner, that.scanner) &&
                Objects.equals(commands, that.commands) &&
                Objects.equals(threadPool, that.threadPool) &&
                Objects.equals(eventsReceived, that.eventsReceived) &&
                Objects.equals(userThread, that.userThread) &&
                Objects.equals(eventHandler, that.eventHandler);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), ownerNameOfTheView, localCopyOfTheStatus, controller, out, scanner, commands, threadPool, eventsReceived, userThread, eventHandler);
    }

}
