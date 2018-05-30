package project.ing.soft.view;

import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.*;
import javafx.util.Pair;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemanager.events.Event;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;


public class ClientViewCLI extends UnicastRemoteObject
        implements IView, IEventHandler, Serializable, IToolCardParametersAcquirer {

    private IGameManager localCopyOfTheStatus;

    private String ownerNameOfTheView;
    private transient IController  controller;
    private transient String token;

    private transient PrintStream out;
    private transient NonBlockingScanner scanner;


    private transient ExecutorService threadPool;
    private final transient Queue<Event> eventsReceived;

    private transient Future actualTurn;
    private transient Future eventDigester;
    private transient Future remoteOperation;

    public ClientViewCLI(String ownerNameOfTheView, IController controller) throws RemoteException {
        this(ownerNameOfTheView);
        this.controller           = controller;
    }

    public ClientViewCLI(String ownerNameOfTheView) throws RemoteException {
        super();

        this.ownerNameOfTheView   = ownerNameOfTheView;
        this.out                  = new PrintStream(System.out);
        this.scanner              = new NonBlockingScanner(System.in);
        this.localCopyOfTheStatus = null;

        this.eventsReceived       = new LinkedList<>();
        this.threadPool           = Executors.newCachedThreadPool();

        eventDigester = threadPool.submit( this::eventHandlingFunction);
    }

    private boolean eventHandlingFunction() throws InterruptedException {
        Event toRespond;

        while(gameOngoing()) {

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

    private boolean gameOngoing(){
        return (localCopyOfTheStatus == null || localCopyOfTheStatus.getStatus() != IGameManager.GAME_MANAGER_STATUS.ENDED);
    }

    @Override
    public void attachController(IController aController){
        this.controller = aController;
    }

    //region event handling

    @Override
    public void update(Event aEvent) {
        out.println( ownerNameOfTheView + " has received an event:" + aEvent);

        if (gameOngoing()) {
            synchronized (eventsReceived) {
                eventsReceived.add(aEvent);
                eventsReceived.notifyAll();
            }
        }
    }


    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        out.println("Now it's others turn");

    }

    @Override
    public void respondTo(FinishedSetupEvent event) {

        out.println("Finished setup.. wait while the game start");
    }
    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        if(actualTurn != null)
            actualTurn.cancel(true);

        actualTurn = threadPool.submit(()-> {
            boolean err;
            do {
                err = false;
                try {
                    out.println("These are the public objectives: ");
                    out.println(Card.drawNear(localCopyOfTheStatus.getPublicObjective().toArray()));
                    out.println("This is your private objective: ");
                    out.println(event.getMyPrivateObjective());
                    WindowPatternCard aCard = (WindowPatternCard) chooseFrom(List.of(event.getOne(), event.getTwo()));
                    int isFront = chooseIndexFrom(List.of(aCard.getFrontPattern(), aCard.getRearPattern()));

                    out.println("Wait for other players to choose their pattern card.");
                    controller.choosePattern(ownerNameOfTheView, aCard, isFront == 1);

                } catch (UserInterruptActionException ex) {
                    out.println("The game can't start until you select a window pattern");
                    err = true;
                } catch (Exception e) {
                    displayError(e);
                }
            } while (err);
            return true;
        });

    }
    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();

        out.println("Modello aggiornato!");
        if (!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)) {
            out.println("It's the turn of " + localCopyOfTheStatus.getCurrentPlayer().getName() + ". Wait for yours.");
        }

    }
    @Override
    public void respondTo(MyTurnStartedEvent event) {
        if(actualTurn != null ) {
            actualTurn.cancel(true);
        }
        actualTurn = threadPool.submit(this:: takeTurn);
    }

    private boolean takeTurn() throws InterruptedException {
        int cmd ;
        boolean actionRequest ;

        List<String> commands = List.of("Place a die",
                "Play a toolcard",
                "Show public objectives",
                "Show my situation",
                "Show my favours",
                "End turn");


        do{
            actionRequest = false;
            displayMySituation();
            out.println("Take your turn " + localCopyOfTheStatus.getCurrentPlayer().getName());

            try {
                cmd = chooseIndexFrom(commands);
            } catch (UserInterruptActionException e) {
                cmd = -1;
            }

            try{

                switch(cmd){
                    case -1:
                        out.println("If you do not want to perform any action, please end your turn.");
                        break;
                    case 0:
                        out.println(localCopyOfTheStatus.getCurrentPlayer());
                        Coordinate placePosition = chooseDieCoordinate("Enter where you want to place your die");
                        Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
                        controller.placeDie(ownerNameOfTheView, chosenDie, placePosition.getRow(), placePosition.getCol());
                        break;
                    case 1:
                        out.println("Choose a toolcard: ");
                        ToolCard aToolCard =  (ToolCard) chooseFrom(localCopyOfTheStatus.getToolCards());

                        aToolCard.fill(this);
                        controller.playToolCard(ownerNameOfTheView, aToolCard);
                        break;
                    case 2:
                        out.println("Public objectives: ");
                        out.println(Card.drawNear(localCopyOfTheStatus.getPublicObjective()));
                        break;
                    case 3:
                        displayMySituation();

                        break;
                    case 4:
                        out.println("You still have " + localCopyOfTheStatus.getFavours().get(localCopyOfTheStatus.getCurrentPlayer().getName()));
                        break;
                    case 5:
                        controller.endTurn(ownerNameOfTheView);
                        break;

                    default:
                        out.println("No operation performed");

                }

            }
            catch(UserInterruptActionException e){
                out.println("Operation aborted. Please select an action");
                update(new MyTurnStartedEvent());
            }catch(InterruptedException ignored){
                out.println("Timeout expired. Your turn ended. Too bad :(");
                throw ignored;
            } catch (Exception e) {
                displayError(e);
            }

        }
        while(cmd != 5 && cmd != 1 && cmd != 0 );

        return true;
    }

    @Override
    public void respondTo(ToolcardActionRequestEvent event) {
        if(actualTurn!=null)
            actualTurn.cancel(true);
        actualTurn = threadPool.submit(()->toolCardAction(event));

    }

    private boolean toolCardAction(ToolcardActionRequestEvent event) {
        out.println("Server request other parameter to complete the ToolCard");
        boolean done = false;
        do {
            ToolCard aToolCard = event.getCard();
            try {
                aToolCard.fill(this);
                controller.playToolCard(ownerNameOfTheView, aToolCard);
                done = true;
            } catch (UserInterruptActionException ignored) {
                //This happens when the user stop the action
            } catch (Exception ex) {
                displayError(ex);
            }
        }while(!done);
        return true;
    }

    @Override
    public void respondTo(SetTokenEvent event) {
        this.token = event.getToken();
        out.println("Connection established. Please, wait for the game to start");
        out.println("Please remember to save this code to let you ask for reconnection in case of network problems");
        out.println("YOUR TOKEN TO ASK RECONNECTION IS: " + token);
    }

    @Override
    public void respondTo(MyTurnEndedEvent event){
        out.println("You had plenty of time and you didn't used.. the turn was ended by the server");
        actualTurn.cancel(true);

        out.println(actualTurn.isCancelled());

    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        out.println("Game finished!");
        out.println("Final Rank:");

        for (Pair<Player, Integer> aPair : event.getRank()){
            out.println(aPair.getKey() + " => " + aPair.getValue());
        }
    }

    //endregion

    //region helper functions

    private void displayError(Exception ex){
        out.println("Error: "+ex.getMessage());
        Scanner input = new Scanner(System.in);

        out.println("Do you need stack trace? [y/n]");

        if(input.next().startsWith("y"))
            ex.printStackTrace(out);
    }

    private void displayMySituation(){
        out.println("Turn: "+localCopyOfTheStatus.getRoundTracker().getCurrentRound());
        // Print only the situation of the current player, i.e. the owner of this view.
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                out.println(p);
            }
        }
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
        out.println("RoundTracker dice left : "+ localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound());
    }

    private void displayEntireGameBoard(){
        out.println("Public objectives");
        out.println(Card.drawNear(localCopyOfTheStatus.getPublicObjective()));
        out.println("ToolCards");
        out.println(Card.drawNear(localCopyOfTheStatus.getToolCards()));

        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            out.println(p);
        }
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
    }

    @Override
    public void run() {
        this.attachController(controller);

        out.println(ownerNameOfTheView + " started ");
        out.println("Waiting for enough players to start the match...");

    }
//endregion

    //region user input

    private int waitForUserInput(int lowerBound , int upperBound) throws UserInterruptActionException, InterruptedException {
        int ret = lowerBound;
        boolean err;

        do{
            err = false;
            try{

                ret = Integer.valueOf(scanner.readLine());
            }
            catch( NumberFormatException e){
                err = true;
            }
            err = err || ret < lowerBound || ret > upperBound;

            if(err){
                if(scanner.readLine().startsWith("q"))
                    throw new UserInterruptActionException();
                out.println("You entered a value that does not fit into the correct interval. Enter q to interrupt the operation");

            }
        }while(err);

        return ret;
    }

    private Coordinate chooseDieCoordinate (String caption) throws UserInterruptActionException, InterruptedException {
        out.println(caption);
        int row = 0;
        int col = 0;
        out.println("Row Index [0 - 3]");
        row = waitForUserInput(0, 3);
        out.println("Col Index [0 - 4]");
        col = waitForUserInput(0, 4);
        return new Coordinate(row, col);

    }

    private Object chooseFrom(List objs) throws UserInterruptActionException, InterruptedException {
        return objs.get(chooseIndexFrom(objs));
    }

    private int chooseIndexFrom(List objs) throws UserInterruptActionException, InterruptedException {

        out.println(String.format("Enter a number between 0 and %d to select:", objs.size()-1));
        for (int i = 0; i < objs.size() ; i++) {
            out.println(String.format("[%d] for %s", i, objs.get(i).toString()));
        }
        return waitForUserInput(0, objs.size()-1);

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
        return (int) chooseFrom(Arrays.asList(values));
    }

    //endregion

    public void stop(){
        threadPool.shutdown();

    }
}
