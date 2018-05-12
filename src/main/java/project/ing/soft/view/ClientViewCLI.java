package project.ing.soft.view;

import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.cards.Card;
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
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class ClientViewCLI extends UnicastRemoteObject implements IView, IEventHandler, Serializable {

    private IGameManager localCopyOfTheStatus;

    private String ownerNameOfTheView;
    private transient IController  controller;

    private transient PrintStream out;
    private transient NonBlockingScanner scanner;
    private transient CliToolCardFiller aToolCardFiller;

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
        this.aToolCardFiller      = new CliToolCardFiller( scanner, System.out, null);
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

            if (toRespond != null)
                toRespond.accept(this);

            synchronized (eventsReceived) {
                eventsReceived.notifyAll();
            }
        }
        //this return
        return true;
    }

    private boolean gameOngoing(){
        return (localCopyOfTheStatus == null || !localCopyOfTheStatus.isFinished());
    }

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
    public void respondTo(PlaceThisDieEvent event){
        actualTurn.cancel(true);

        actualTurn = threadPool.submit(() -> {
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
            }
            actualTurn = threadPool.submit(this::takeTurn);
        });

    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        out.println("Now it's others turn");
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
    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        boolean err;
        do {
            err = false;
            try {
                out.println("This is your private objective: ");
                out.println(event.getMyPrivateObjective());
                WindowPatternCard aCard = (WindowPatternCard) chooseFrom(List.of(event.getOne(), event.getTwo()));
                int isFront = chooseIndexFrom(List.of(aCard.getFrontPattern(), aCard.getRearPattern()));

                out.println("Wait for other players to choose their pattern card.");
                controller.choosePattern(ownerNameOfTheView, aCard, isFront == 1);

            } catch (UserInterruptActionException ex) {
                out.println("The game can't start until you select a window pattern");
                err = true;
            } catch (InterruptedException e) {
                displayError(e);

            }catch (Exception otherException){
                err = true;
            }
        }while (err);

    }

    @Override
    public void respondTo(MyTurnEndedEvent event){
        out.println("You had plenty of time and you didn't used.. the turn was ended by the server");
        actualTurn.cancel(true);

        out.println(actualTurn.isCancelled());

    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        actualTurn = threadPool.submit(this:: takeTurn);
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();

        if(actualTurn != null && !actualTurn.isDone()) {
            actualTurn.cancel(true);
            actualTurn = threadPool.submit(this:: takeTurn);
        }

        out.println("Modello aggiornato!");
        if (!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)) {
            out.println("It's the turn of " + localCopyOfTheStatus.getCurrentPlayer().getName() + ". Wait for yours.");
        }

    }

    private void displayError(Exception ex){
        out.println("Error: "+ex.getMessage());
        Scanner input = new Scanner(System.in);

        out.println("Do you need stack trace? [y/n]");

        if(input.next().startsWith("y"))
            ex.printStackTrace(out);
    }

    @Override
    public void attachController(IController aController){
        this.controller = aController;
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
    public void run() throws Exception {
        this.attachController(controller);

        out.println(ownerNameOfTheView + " started ");
        out.println("Waiting for enough players to start the match...");

    }

    private boolean takeTurn() throws InterruptedException {
        int cmd ;


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

                        remoteOperation = threadPool.submit(() -> {
                            controller.placeDie(ownerNameOfTheView, chosenDie, placePosition.getRow(), placePosition.getCol());
                            return true;
                        });
                        remoteOperation.get();
                        break;
                    case 1:
                        out.println("Choose a toolcard: ");
                        ToolCard aToolCard =  (ToolCard) chooseFrom(localCopyOfTheStatus.getToolCards());
                        aToolCardFiller.updateLocalCopyOfModel(localCopyOfTheStatus);
                        aToolCardFiller.fill(aToolCard);

                        remoteOperation = threadPool.submit(() -> {
                            controller.playToolCard(ownerNameOfTheView, aToolCard);
                            return true;
                        });
                        remoteOperation.get();
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
            }catch(InterruptedException e){
                out.println("Timeout expired. Your turn ended. Too bad :(");
            } catch (Exception e) {
                displayError(e);
            }

        }
        while(cmd != 5 && cmd != 1 && cmd != 0 );

        return true;
    }




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

    public void stop(){
        if (eventDigester!= null){
            eventDigester.cancel(true);
        }

        if(remoteOperation != null ){
            eventDigester.cancel(true);
        }

        if( actualTurn != null){
            actualTurn.cancel(true);
        }

    }
}
