package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.Objectives.Publics.PublicObjective;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;
import projectIngSoft.Coordinate;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.events.*;
import projectIngSoft.events.Event;

import java.io.PrintStream;
import java.util.*;
import java.util.List;

public class LocalViewCli implements IView, IEventHandler, IToolCardFiller {
    private IGameManager localCopyOfTheStatus;
    private IController  controller;
    private String ownerNameOfTheView;
    private boolean stopResponding = false;
    private PrintStream out;


    public LocalViewCli(String ownerNameOfTheView) {
        // TODO: come fa una view a sapere chi è il suo "padrone" (player)?
        // é necessario che lo sappia?? meglio condividere un codice che attesti semplicemente la'utenticità del client
        //questo perchè il problema sarebbe l'invocazioni di metodi remoti tramite rmi. non posso identificarlo

        // getCurrentPlayer da solo il giocatore di turno non il giocatore della view
        this.ownerNameOfTheView = ownerNameOfTheView;
        out = System.out;
    }


    @Override
    public void update(Event aEvent) {
        out.println( ownerNameOfTheView + " ha ricevuto un evento :" + aEvent);
        if (!stopResponding) {
            aEvent.accept(this);
        }
    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {

    }

    @Override
    public void respondTo(FinishedSetupEvent event) {

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
        Pair<WindowPatternCard, Boolean> chosenCouple;
        while(true) {
            try {

                WindowPatternCard aCard = (WindowPatternCard) chooseFrom(List.of(event.getOne(), event.getTwo()));
                int isFront = chooseIndexFrom(List.of(aCard.getFrontPattern(), aCard.getRearPattern()));
                chosenCouple = new Pair<>(aCard, isFront == 1);


                controller.choosePattern(ownerNameOfTheView, chosenCouple);
                return;
            } catch (InterruptActionException ex) {
                out.println("The game can't start until you select a window pattern");
            } catch (Exception e) {
                displayError(e);
            }
        }

    }

    @Override
    public void respondTo(myTurnStartedEvent event) {
        try {
            takeTurn();
        } catch (Exception e) {
            displayError(e);
        }
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();
        displayMySituation();
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
            System.out.println(p);
        }*/
        out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
    }


    private void takeTurn() throws Exception {
        int cmd = -1;
        List<String> commands = List.of("Place a die",
                                        "Play a toolcard",
                                        "Show public objectives",
                                        "Show my situation",
                                        "Show my favours",
                                        "End turn");



        do {
            displayMySituation();
            out.println("Take your turn " + localCopyOfTheStatus.getCurrentPlayer().getName());

            try{
                cmd = chooseIndexFrom(commands);

            }
            catch(InterruptActionException e){
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
                        controller.endTurn();
                        break;
                    default:
                        out.println("No operation performed");

                }

            }
            catch(InterruptActionException e){
                out.println("Operation aborted. Please select an action");
            }
            catch(Exception e){
                displayError(e);
            }
        }
        while(cmd != 5);
    }



    private int waitForUserInput(int lowerBound , int upperBound) throws InterruptActionException {
        int ret = 0;
        boolean err;
        Scanner input = new Scanner(System.in);

        do{
            err = false;
            try{
                ret = input.nextInt();
            }
            catch(InputMismatchException e){
                err = true;
            }
            err = err || ret < lowerBound || ret > upperBound;

            if(err){
                if(input.nextLine().startsWith("q"))
                    throw new InterruptActionException();
                out.println("You entered a value that does not fit into the correct interval. Enter q to interrupt the operation");

            }


        }while(err);




        return ret;
    }

    private Coordinate chooseDieCoordinate (String caption) {
        out.println(caption);
        int row = 0;
        int col = 0;
        try {
            out.println("Row Index [0 - 3]");
            row = waitForUserInput(0, 3);
            out.println("Col Index [0 - 4]");
            col = waitForUserInput(0, 4);
        } catch (Exception e){
            out.println("Default position row: 0 col:0 assumed");
            row = 0;
        }
        return new Coordinate(row, col);

    }

    private Object chooseFrom(List objs) throws InterruptActionException {
        return objs.get(chooseIndexFrom(objs));
    }

    private int chooseIndexFrom(List objs) throws InterruptActionException {

        out.println(String.format("Enter a number between 0 and %d to select:", objs.size()-1));
        for (int i = 0; i < objs.size() ; i++) {
            out.println(String.format("[%d] for %s", i, objs.get(i).toString()));
        }
        return waitForUserInput(0, objs.size()-1);

    }

    @Override
    public AlesatoreLaminaRame fill(AlesatoreLaminaRame aToolcard) {
        out.println(aToolcard);
        aToolcard.setStartPosition(chooseDieCoordinate("Enter which die you want to move"));
        aToolcard.setEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));
        return null;
    }

    @Override
    public DiluentePastaSalda fill(DiluentePastaSalda aToolcard) {
        return null;
    }

    @Override
    public Lathekin fill(Lathekin aToolcard) {
        out.println(aToolcard);
        aToolcard.setFirstDieStartPosition(chooseDieCoordinate("Enter which is the first die you want to move"));
        aToolcard.setFirstDieEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));
        aToolcard.setSecondDieStartPosition(chooseDieCoordinate("Enter which is the second die you want to move"));
        aToolcard.setSecondDieEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));
        return null;
    }

    @Override
    public Martelletto fill(Martelletto aToolcard) {
        return null;
    }

    @Override
    public PennelloPastaSalda fill(PennelloPastaSalda aToolcard) {
        out.println(aToolcard);
        Die chosenDie;
        try {
            chosenDie =  (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
            aToolcard.setToRoll(chosenDie);
        } catch (Exception e) {
            displayError(e);
        }
        return null;
    }

    @Override
    public PennelloPerEglomise fill(PennelloPerEglomise aToolcard) {
        out.println(aToolcard);
        aToolcard.setStartPosition(chooseDieCoordinate("Enter which die you want to move"));
        aToolcard.setEndPosition(chooseDieCoordinate("Enter an empty cell's position to move it"));
        return null;
    }

    @Override
    public PinzaSgrossatrice fill(PinzaSgrossatrice aToolcard) {
        out.println(aToolcard);
        Die chosenDie;
        boolean toBeIncreased;
        try {
            chosenDie =  (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
            aToolcard.setChoosenDie(chosenDie);
            toBeIncreased = (chooseIndexFrom(List.of("Decrease its value", "Increase its value")) == 1);
            aToolcard.setToBeIncreased(toBeIncreased);
        }  catch (Exception e) {
            displayError(e);
        }
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
        out.println(aToolcard);
        Die chosenDieFromDraft;
        Die chosenDieFromRoundTracker;
        try {
            out.println("Chose from Draft:");
            chosenDieFromDraft =  (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());
            aToolcard.setDieFromDraft(chosenDieFromDraft);
            out.println("Chose from RoundTracker:");
            chosenDieFromRoundTracker =  (Die) chooseFrom(localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound());
            aToolcard.setDieFromRoundTracker(chosenDieFromRoundTracker);
        } catch (Exception e) {
            displayError(e);
        }
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

    private static class InterruptActionException extends Exception {
    }
}
