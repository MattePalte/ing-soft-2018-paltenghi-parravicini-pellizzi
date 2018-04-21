package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.events.*;
import projectIngSoft.events.Event;

import java.util.*;
import java.util.List;

public class LocalViewCli implements IView, IEventHandler, IToolCardFiller {
    private IGameManager localCopyOfTheStatus;
    private IController  controller;
    private String ownerNameOfTheView;


    public LocalViewCli(String ownerNameOfTheView) {
        // TODO: come fa una view a sapere chi è il suo "padrone" (player)?
        // é necessario che lo sappia?? meglio condividere un codice che attesti semplicemente la'utenticità del client
        //questo perchè il problema sarebbe l'invocazioni di metodi remoti tramite rmi. non posso identificarlo

        // getCurrentPlayer da solo il giocatore di turno non il giocatore della view
        this.ownerNameOfTheView = ownerNameOfTheView;
    }


    @Override
    public void update(Event aEvent) {
        System.out.println( ownerNameOfTheView + " ha ricevuto un evento :" + aEvent);
        aEvent.accept(this);
    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        /*
        if (localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)){
            displayMySituation();
            try {
                takeTurn();
            } catch (Exception e) {
                displayError(e);
            }
        }*/
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        /*
        if (localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)){
            displayMySituation();
        }*/
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        System.out.println("Game finished!");

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
                System.out.println("The game can't start until you select a window pattern");
            } catch (Exception e) {
                displayError(e);
            }
        }

    }

    @Override
    public void respondTo(myTurnStartedEvent event) {

        displayMySituation();
        try {

            takeTurn();
        } catch (Exception e) {
            displayError(e);
        }

    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();

    }

    private void displayError(Exception ex){
        System.out.println("Error:"+ex.getMessage());
        Scanner input = new Scanner(System.in);

        System.out.println("Do you need stack trace? [y/n]");

        if(input.next().startsWith("y"))
            ex.printStackTrace();
    }

    @Override
    public void attachController(IController aController){
        // TODO: come fa la view a sapere a che controller deve parlare?
        this.controller = aController;
    }



    private void displayMySituation(){
        System.out.println("Turn:"+localCopyOfTheStatus.getRoundTracker().getCurrentRound());
        // Stampa solo situazione attuale del giocatore attuale
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                System.out.println(p);
            }
        }
        System.out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
    }

    private void displayEntireGameBoard(){
        // TODO: abilitare stampa di tutti i giocatori su tutti i client con il codice sotto
        /*for (Player p : localCopyOfTheStatus.getPlayerList()) {
            System.out.println(p);
        }*/
        System.out.println("Draft pool : "+ localCopyOfTheStatus.getDraftPool());
    }


    private void takeTurn() throws Exception {
        int cmd;

        do {
            System.out.println("Take your turn " + localCopyOfTheStatus.getCurrentPlayer().getName());
            cmd = chooseIndexFrom(List.of("Place a die", "Play a toolcard", "End turn"));
            try {
                if (cmd == 0) {
                    System.out.println(localCopyOfTheStatus.getCurrentPlayer());
                    System.out.println("Enter where you want to place your die ");
                    System.out.println("Row Index [0 - 3]");
                    int rowIndex = waitForUserInput(0, 3);
                    System.out.println("Col Index [0 - 4]");
                    int colIndex = waitForUserInput(0, 4);
                    Die chosenDie = (Die) chooseFrom(localCopyOfTheStatus.getDraftPool());

                    controller.placeDie(ownerNameOfTheView, chosenDie, rowIndex, colIndex);

                }
                else if (cmd == 1) {
                    System.out.println("Choose a toolcard: ");
                    ToolCard aToolCard =  (ToolCard) chooseFrom(localCopyOfTheStatus.getToolCards());
                    aToolCard.fill(this);
                    controller.playToolCard(ownerNameOfTheView, aToolCard);
                }
                else {
                    controller.endTurn();
                }
            }
            catch(InterruptActionException e){
                System.out.println("Operation was aborted by the user");
            }
            catch(Exception e){
                displayError(e);
            }
        }
        while(cmd != 3);
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
                System.out.println("Hai inserito un valore errato. Enter q to esc");
                if(input.nextLine().startsWith("q"))
                    throw new InterruptActionException();
            }


        }while(err);




        return ret;
    }
    private Object chooseFrom(List objs) throws InterruptActionException {
        return objs.get(chooseIndexFrom(objs));
    }

    private int chooseIndexFrom(List objs) throws InterruptActionException {

        System.out.println(String.format("Enter a number between 0 and %d to select:", objs.size()-1));
        for (int i = 0; i < objs.size() ; i++) {
            System.out.println(String.format("[%d] for %s", i, objs.get(i).toString()));
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

    private static class InterruptActionException extends Exception {
    }
}
