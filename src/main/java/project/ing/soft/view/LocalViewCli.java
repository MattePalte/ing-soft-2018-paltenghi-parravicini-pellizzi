package project.ing.soft.view;

import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class LocalViewCli extends UnicastRemoteObject implements IView, IEventHandler, IToolCardFiller, Serializable {
    private IGameManager localCopyOfTheStatus;
    private IController  controller;
    private String ownerNameOfTheView;
    private boolean stopResponding = false;
    private transient PrintStream out;
    private final transient Queue<Event> eventsReceived;
    private transient ExecutorService turnExecutor;
    private transient Future actualTurn;
    private transient Future eventWaitingForInput;
    private transient Thread eventHandler;

    public LocalViewCli(String ownerNameOfTheView) throws RemoteException {
        // getCurrentPlayer da solo il giocatore di turno non il giocatore della view
        this.ownerNameOfTheView = ownerNameOfTheView;
        out = new PrintStream(System.out);
        eventsReceived = new LinkedList<>();
        turnExecutor = Executors.newFixedThreadPool(2);

        eventHandler = new Thread( () -> {
            Event toRespond = null;

            while(true) {
                synchronized (eventsReceived) {
                    try {
                        while (eventsReceived.isEmpty())
                            eventsReceived.wait();
                        toRespond = eventsReceived.remove();
                    } catch (InterruptedException e) {
                        displayError(e);
                    }
                }
                if (toRespond != null)
                    toRespond.accept(this);
                toRespond = null;
                synchronized (eventsReceived) {
                    eventsReceived.notifyAll();
                }
            }
        });

        eventHandler.start();
    }


    @Override
    public void update(Event aEvent) {
        out.println( getTime() + " - " + ownerNameOfTheView + " ha ricevuto un evento :" + aEvent);

        if (!stopResponding) {
            synchronized (eventsReceived) {
                eventsReceived.add(aEvent);
                eventsReceived.notifyAll();
            }
            //aEvent.accept(this);
        }
    }

    private String getTime() {
        Calendar c = Calendar.getInstance(); //automatically set to current time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(c.getTime()).toString();
        return time;
    }

    @Override
    public void respondTo(PlaceThisDieEvent event){
        eventWaitingForInput = turnExecutor.submit(() -> {
            Coordinate chosenPosition = null;
            Die toBePlaced = event.getToBePlaced();

            try {
                if (event.getIsValueChoosable()) {
                    System.out.println("You draft a " + toBePlaced.getColour() + " die. Choose the die value");
                    int newValue = waitForUserInput(1, 6);
                    toBePlaced = new Die(newValue, toBePlaced.getColour());
                    System.out.println("Die to be placed: ");
                }
            } catch (UserInterruptActionException e) {
                System.out.println("You didn't choose the die value. The die has been rolled");
            } catch (InterruptedException e) {
                System.out.println("Timeout expired. Your turn ended");
                return;
            }
            do {
                try {
                    out.println("Choose a position where to place this die: " + toBePlaced);
                    chosenPosition = (Coordinate) chooseFrom(event.getCompatiblePositions());
                } catch (UserInterruptActionException e) {
                    chosenPosition = null;
                    out.println("You must choose where to place this die: " + toBePlaced);
                } catch (InterruptedException e) {
                    out.println("Timeout expired. Your turn ended.");
                    return;
                }
            }
            while (chosenPosition == null);
            try {
                controller.placeDie(ownerNameOfTheView, toBePlaced, chosenPosition.getRow(), chosenPosition.getCol());
            } catch (Exception e) {
                displayError(e);
                respondTo(new MyTurnStartedEvent());
            }
        });
    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        return;
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        return;
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        out.println("Game finished!");
        out.println("Final Rank:");

        for (Pair<Player, Integer> aPair : event.getRank()){
            out.println(aPair.getKey() + " => " + aPair.getValue());
        }
        stopResponding = true;

    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {

        while(true) {
            try {
                out.println("This is your private objective: ");
                out.println(event.getMyPrivateObjective());
                WindowPatternCard aCard = (WindowPatternCard) chooseFrom(List.of(event.getOne(), event.getTwo()));
                int isFront = chooseIndexFrom(List.of(aCard.getFrontPattern(), aCard.getRearPattern()));

                out.println("Wait for other players to choose ther pattern card.");
                controller.choosePattern(ownerNameOfTheView, aCard, isFront == 1);

                return;
            } catch (UserInterruptActionException ex) {
                out.println("The game can't start until you select a window pattern");
            } catch (Exception e) {
                displayError(e);
            }
        }

    }

    @Override
    public void respondTo(MyTurnEndedEvent event){
        actualTurn.cancel(true);
        if(eventWaitingForInput != null && !eventWaitingForInput.isDone())
            eventWaitingForInput.cancel(true);
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        actualTurn = turnExecutor.submit(() -> {
            try {
                takeTurn();
            } catch (Exception e) {
                displayError(e);
            }
        });
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();
        out.println("Modello aggiornato!");
        if (!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)) {
            out.println("It's the turn of " + localCopyOfTheStatus.getCurrentPlayer().getName() + ". Wait for yours.");
        }
        //displayMySituation();
    }

    private void displayError(Exception ex){
        out.println("Error:"+ex.getMessage());
        Scanner input = new Scanner(System.in);

        out.println("Do you need stack trace? [y/n]");

        if(input.next().startsWith("y"))
            ex.printStackTrace();
    }

    @Override
    public void attachController(IController aController){
        // TODO: come fa la view a sapere a che controller deve parlare?
        this.controller = aController;
    }



    private void displayMySituation(){
        out.println("Turn:"+localCopyOfTheStatus.getRoundTracker().getCurrentRound());
        // Stampa solo situazione attuale del giocatore attuale
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                out.println(p);
            }
        }
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
        out.println("RoundTracker dice left : "+ localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound());
    }

    private void displayEntireGameBoard(){
        // TODO: abilitare stampa di tutti i giocatori su tutti i client con il codice sotto
        /*for (Player p : localCopyOfTheStatus.getPlayerList()) {
            out.println(p);
        }*/
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
    }

    @Override
    public void run() throws Exception {
        out.println(ownerNameOfTheView + " started ");
        out.println("Waiting for enought players to start the match...");
    }

    private void takeTurn() {
        int cmd = -1;
        ExecutorService opExecutor = Executors.newSingleThreadExecutor();

        List<String> commands = List.of("Place a die",
                "Play a toolcard",
                "Show public objectives",
                "Show my situation",
                "Show my favours",
                "End turn");



        do{
            displayMySituation();
            out.println("Take your turn " + localCopyOfTheStatus.getCurrentPlayer().getName());

            try{
                cmd = chooseIndexFrom(commands);

            }
            catch(UserInterruptActionException e){
                out.println("If you do not want to perform any action, please end your turn.");
                cmd = -1;
            } catch (InterruptedException ignored) {
                System.out.println("Timeout expired. Your turn ended");
                return;
            }

            try{
                Future fut;
                switch(cmd){
                    case 0:
                        out.println(localCopyOfTheStatus.getCurrentPlayer());
                        Coordinate placePosition = chooseDieCoordinate("Enter where you want to place your die");
                        Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());

                        fut = opExecutor.submit(() -> {
                            controller.placeDie(ownerNameOfTheView, chosenDie, placePosition.getRow(), placePosition.getCol());
                            return true;
                        });
                        fut.get();
                        break;
                    case 1:
                        out.println("Choose a toolcard: ");
                        ToolCard aToolCard =  (ToolCard) chooseFrom(localCopyOfTheStatus.getToolCards());
                        aToolCard.fill(this);
                        fut = opExecutor.submit(() -> {
                            controller.playToolCard(ownerNameOfTheView, aToolCard);
                            return true;
                        });
                        fut.get();
                        break;
                    case 2:
                        out.println("Public objectives: ");
                        for(PublicObjective card : localCopyOfTheStatus.getPublicObjective())
                            out.println(card);
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
            }catch(InterruptedException e){
                out.println("Timeout expired. Your turn ended. Too bad :(");
            } catch (Exception e) {
                displayError(e);
                cmd = -1;
            }
            /*catch(Exception e){
                displayError(e);
            }*/
        }
        while(cmd != 5 && cmd != 1 && cmd != 0 );
    }



    private String preemptiveReadline() throws InterruptedException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String input = "";
        do {

            try {
                // wait until we have data to complete a readLine()
                while (!br.ready()) {
                    //Thread.sleep(500);
                    if(Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                }
                input = br.readLine();
            }catch (IOException e) {
                e.printStackTrace(out);
            }
        } while ("".equals(input));

        return  input;
    }

    private int waitForUserInput(int lowerBound , int upperBound) throws UserInterruptActionException, InterruptedException {
        int ret = 0;
        boolean err;
        String in = null;


        do{
            err = false;
            try{
                in = preemptiveReadline();
                ret = Integer.valueOf(in);
            }
            catch( NumberFormatException e){
                err = true;
            }
            err = err || ret < lowerBound || ret > upperBound;

            if(err){
                if(in.startsWith("q"))
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

    @Override
    public void fill(AlesatoreLaminaRame aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        aToolcard.setStartPosition(chooseDieCoordinate("Enter which die you want to move"));
        aToolcard.setEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));

    }

    @Override
    public void fill(DiluentePastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        out.println("Choose a die to take back to the dicebag: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
        aToolcard.setChosenDie(chosenDie);
    }

    @Override
    public void fill(Lathekin aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        aToolcard.setFirstDieStartPosition(chooseDieCoordinate("Enter which is the first die you want to move"));
        aToolcard.setFirstDieEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));
        aToolcard.setSecondDieStartPosition(chooseDieCoordinate("Enter which is the second die you want to move"));
        aToolcard.setSecondDieEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));

    }

    @Override
    public void fill(Martelletto aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(PennelloPastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        Die chosenDie;
        chosenDie =  (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
        aToolcard.setToRoll(chosenDie);

    }

    @Override
    public void fill(PennelloPerEglomise aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        aToolcard.setStartPosition(chooseDieCoordinate("Enter which die you want to move"));
        aToolcard.setEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));
    }

    @Override
    public void fill(PinzaSgrossatrice aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        Die chosenDie;
        boolean toBeIncreased;
        chosenDie =  (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
        aToolcard.setChoosenDie(chosenDie);
        toBeIncreased = (chooseIndexFrom(List.of("Decrease its value", "Increase its value")) == 1);
        aToolcard.setToBeIncreased(toBeIncreased);

    }

    @Override
    public void fill(RigaSughero aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        out.println("Choose a die from the draftpool: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
        aToolcard.setChosenDie(chosenDie);
        Coordinate chosenPosition = chooseDieCoordinate("Choose a position away from other dice: ");
        aToolcard.setPosition(chosenPosition);

    }

    @Override
    public void fill(StripCutter aToolcard) {

    }

    @Override
    public void fill(TaglierinaManuale aToolcard) throws InterruptedException, UserInterruptActionException {
        ArrayList<Coordinate> positions = null;
        ArrayList<Coordinate> moveTo = null;

        out.println(aToolcard);
        out.println("Choose a die from the roundtracker: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound());
        aToolcard.setDieFromRoundTracker(chosenDie);
        positions = new ArrayList<>();
        moveTo = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            positions.add(chooseDieCoordinate("Choose the position of a " + chosenDie.getColour() + " placed die in your pattern"));
            moveTo.add(chooseDieCoordinate("Choose where you want to move the die you have just chosen"));
        }
        aToolcard.setDiceChosen(positions);
        aToolcard.setMoveTo(moveTo);
        //TODO: find a way to let players choose to select just one die

    }

    @Override
    public void fill(TaglierinaCircolare aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        Die chosenDieFromDraft;
        Die chosenDieFromRoundTracker;
        out.println("Chose from Draft:");
        chosenDieFromDraft =  (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
        aToolcard.setDieFromDraft(chosenDieFromDraft);
        out.println("Chose from RoundTracker:");
        chosenDieFromRoundTracker =  (Die) chooseFrom(localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound());
        aToolcard.setDieFromRoundTracker(chosenDieFromRoundTracker);

    }

    @Override
    public void fill(TamponeDiamantato aToolcard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolcard);
        out.println("Choose a die from the draftpool: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
        aToolcard.setChosenDie(chosenDie);

    }

    @Override
    public void fill(TenagliaRotelle aToolcard) throws InterruptedException, UserInterruptActionException {

    }
}
