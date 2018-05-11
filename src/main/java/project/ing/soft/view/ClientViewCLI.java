package project.ing.soft.view;


import project.ing.soft.cards.objectives.publics.PublicObjective;
import project.ing.soft.cards.toolcards.*;
import project.ing.soft.Coordinate;
import project.ing.soft.Die;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.events.*;
import javafx.util.Pair;
import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.controller.IController;
import project.ing.soft.events.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class ClientViewCLI extends Thread implements IView, IEventHandler, IToolCardFiller {
    private IGameManager localCopyOfTheStatus;
    private IController  controller;
    private String ownerNameOfTheView;
    private boolean stopResponding = false;
    private PrintStream out;
    private final ArrayList<Event> events;
    private final ExecutorService eventHandlerExecutor;
    private Future fut;



    public ClientViewCLI(String ownerNameOfTheView) {
        // getCurrentPlayer da solo il giocatore di turno non il giocatore della view
        this.ownerNameOfTheView = ownerNameOfTheView;
        this.out = new PrintStream(System.out);
        this.events= new ArrayList<>() ;
        this.eventHandlerExecutor = Executors.newSingleThreadExecutor();

    }


    @Override
    public void update(Event aEvent) {

        synchronized(events) {
            out.println(ownerNameOfTheView + " ha ricevuto un evento :" + aEvent);

            //if it's a modelChangedEvent has a greater priority
            if (aEvent instanceof ModelChangedEvent && fut != null &&!fut.isDone()) {
                events.add(0, aEvent);
                fut.cancel(true);

            } else {
                events.add(aEvent);
            }
        }


        fut = eventHandlerExecutor.submit(this::flushEvents);

    }

    private void flushEvents() {
        while(!events.isEmpty()) {
            Event aEvent = null;
            synchronized (events) {
                if (!events.isEmpty()) {
                    aEvent = events.get(0);
                }
            }
            //the accept method has the responsibility of drawing the events queue
            if (aEvent != null) {
                aEvent.accept(this);
            }
        }

    }


    @Override
    public void respondTo(PlaceThisDieEvent event) {

    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        synchronized (events){
            events.remove(0);
        }
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        synchronized (events){
            events.remove(0);
        }
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        out.println("Game finished!");
        out.println("Final Rank:");

        for (Pair<Player, Integer> aPair : event.getRank()){
            out.println(aPair.getKey() + " => " + aPair.getValue());
        }
        stopResponding = true;

        synchronized (events){
            events.remove(0);
        }
    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        boolean notAlreadyChosenACard = true;
        while(notAlreadyChosenACard) {
            try {

                WindowPatternCard aCard = (WindowPatternCard) chooseFrom(List.of(event.getOne(), event.getTwo()));
                int isFront = chooseIndexFrom(List.of(aCard.getFrontPattern(), aCard.getRearPattern()));

                out.println("Wait for other players to choose their pattern card.");
                controller.choosePattern(ownerNameOfTheView, aCard, isFront == 1);

                notAlreadyChosenACard = false;
            }catch (InterruptedException e){
                return;
            } catch (UserInterruptActionException ex) {
                out.println("The game can't start until you select a window pattern");
            } catch (Exception e) {
                displayError(e);
            }
        }

        synchronized (events){
            events.remove(0);
        }

    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        try {
            takeTurn();

            synchronized (events){
                events.remove(0);
            }

        } catch(InterruptedException interrupt){
            return;
        }catch (Exception e) {
            displayError(e);
        }
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();
        out.println("Modello aggiornato!");
        if (!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)) {
            out.println("It's the turn of " + localCopyOfTheStatus.getCurrentPlayer().getName() + ". Wait for yours.");
        }

        synchronized (events){
            events.remove(0);
        }

    }

    @Override
    public void respondTo(TurnEndedEvent event) {
        //TODO: to implement method
    }

    private void displayError(Exception ex){
        out.println("Error:"+ex.getMessage());
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

        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            out.println(p);
        }
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
    }

    @Override
    public void run() {
        out.println(ownerNameOfTheView + " started ");
        out.println("Waiting for enough players to start the match...");
    }

    private void takeTurn() throws Exception {
        int cmd = -1;
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
            }

            try{
                switch(cmd){
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
                        for(PublicObjective card : localCopyOfTheStatus.getPublicObjective())
                            out.println(card);
                        break;
                    case 3:
                        displayMySituation();
                        break;
                    case 4:
                        out.println("You still have " + localCopyOfTheStatus.getFavours().get(localCopyOfTheStatus.getCurrentPlayer()));
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
            }
            catch (InterruptedException interrupt){
                throw interrupt;
            }
            catch(Exception e){
                displayError(e);
            }
        }
        while(cmd != 5 );
    }



    private int waitForUserInput(int lowerBound , int upperBound) throws UserInterruptActionException, InterruptedException {
        int ret = 0;
        boolean err;



        do{
            err = false;
            try{
                ret = Integer.valueOf(preemptiveReadline());
            }
            catch( NumberFormatException e){
                err = true;
            }
            err = err || ret < lowerBound || ret > upperBound;

            if(err){
                if(preemptiveReadline().startsWith("q"))
                    throw new UserInterruptActionException();
                out.println("You entered a value that does not fit into the correct interval. Enter q to interrupt the operation");

            }


        }while(err);




        return ret;
    }

    public String preemptiveReadline() throws InterruptedException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String input = "";
        do {

            try {
                // wait until we have data to complete a readLine()
                while (!br.ready()) {
                    Thread.sleep(500);
                }
                input = br.readLine();
            }catch (IOException e) {
                e.printStackTrace(out);
            }
        } while ("".equals(input));

        return  input;
    }
    private Coordinate chooseDieCoordinate (String caption) throws InterruptedException {
        out.println(caption);
        int row = 0;
        int col = 0;
        try {
            out.println("Row Index [0 - 3]");
            row = waitForUserInput(0, 3);
            out.println("Col Index [0 - 4]");
            col = waitForUserInput(0, 4);
        } catch(InterruptedException interrupt){
            throw interrupt;
        }catch (Exception e){
            out.println("Default position row: 0 col:0 assumed");
            row = 0;
        }
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
    public AlesatoreLaminaRame fill(AlesatoreLaminaRame aToolcard) {
           return null;
    }

    @Override
    public DiluentePastaSalda fill(DiluentePastaSalda aToolcard) {
        return null;
    }

    @Override
    public Lathekin fill(Lathekin aToolcard) {

          return null;
    }

    @Override
    public Martelletto fill(Martelletto aToolcard) {
        return null;
    }

    @Override
    public PennelloPastaSalda fill(PennelloPastaSalda aToolcard) {

        return null;
    }

    @Override
    public PennelloPerEglomise fill(PennelloPerEglomise aToolcard) {
       return null;
    }

    @Override
    public PinzaSgrossatrice fill(PinzaSgrossatrice aToolcard) {
        return null;
    }

    @Override
    public RigaSughero fill(RigaSughero aToolcard) {
        return null;
    }

    @Override
    public StripCutter fill(StripCutter aToolcard) {
        return null;
    }

    @Override
    public TaglierinaManuale fill(TaglierinaManuale aToolcard) {
        return null;
    }

    @Override
    public TaglierinaCircolare fill(TaglierinaCircolare aToolcard) {
        return null;
    }

    @Override
    public TamponeDiamantato fill(TamponeDiamantato aToolcard) {
        return null;
    }

    @Override
    public TenagliaRotelle fill(TenagliaRotelle aToolcard) {
        return null;
    }

    private static class UserInterruptActionException extends Exception {
    }
}

