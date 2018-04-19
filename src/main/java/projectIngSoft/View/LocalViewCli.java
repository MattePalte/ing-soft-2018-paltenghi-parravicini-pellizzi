package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.*;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.events.*;
import projectIngSoft.exceptions.*;

import java.util.*;

public class LocalViewCli implements IView, IEventHandler, IToolCardFiller {
    private IGameManager localCopyOfTheStatus;
    private IController controller;
    private String ownerNameOfTheView;


    public LocalViewCli(String ownerNameOfTheView) {
        // TODO: come fa una view a sapere chi è il suo "padrone" (player)?
        // é necessario che lo sappia?? meglio condividere un codice che attesti semplicemente la'utenticità del client
        //questo perchè il problema sarebbe l'invocazioni di metodi remoti tramite rmi. non posso identificarlo

        // getCurrentPlayer da solo il giocatore di turno non il giocatore della view
        this.ownerNameOfTheView = ownerNameOfTheView;
    }

    public LocalViewCli() {
        // inutile, presente solo per retrocompatibilità con test che usavano LocalView senza parametri
    }


    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        System.out.println("Evento ricevuto da " + ownerNameOfTheView + " : Giocatore Cambiato");
        if (localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)){
            displayMySituation();
            try {
                takeTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        System.out.println("Evento ricevuto da " + ownerNameOfTheView + " : Setup finito");
        if (localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)){
            displayMySituation();
            try {
                takeTurn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        System.out.println("Evento ricevuto da " + ownerNameOfTheView + " : Game Terminato");
        displayMySituation();
    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        System.out.println("Evento ricevuto da " + ownerNameOfTheView + " : Carte distribuite");
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                Pair<WindowPatternCard, Boolean> chosenCouple = choosePattern(p.getPossiblePatternCard());
                try {
                    controller.choosePattern(ownerNameOfTheView, chosenCouple);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void attachController(IController aController){
        // TODO: come fa la view a sapere a che controller deve parlare?
        this.controller = aController;
    }

    @Override
    public void update(Event event) {

        event.accept(this);
    }

    private void displayMySituation(){
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

    private void endTurn() throws Exception {
        controller.endTurn();
    }

    public Pair<WindowPatternCard, Boolean> choosePattern(List<WindowPatternCard> patternCards){
        int userInput;
        WindowPatternCard chosenPatternCard;
        Boolean isFlipped;

        System.out.println(ownerNameOfTheView + ", choose a pattern card: ");
        System.out.println("1 -\n" + patternCards.get(0));
        System.out.println("2 - \n" + patternCards.get(1));
        userInput = waitForUserInput(1,2);
        if(userInput == 1){
            chosenPatternCard = patternCards.get(0);
            isFlipped = chooseFace(chosenPatternCard);
        }
        else {
            chosenPatternCard = patternCards.get(1);
            isFlipped = chooseFace(chosenPatternCard);
        }
        return new Pair<WindowPatternCard, Boolean>(chosenPatternCard, isFlipped);
    }

    private void takeTurn() throws Exception {
        int cmd;

        do {
            System.out.println("Take your turn " + localCopyOfTheStatus.getCurrentPlayer().getName());
            System.out.println("1 - Place a die");
            System.out.println("2 - Play a toolcard");
            System.out.println("3 - End your turn");
            cmd = waitForUserInput(1,3);

            if (cmd == 1) {
                try {
                    if (localCopyOfTheStatus.getCurrentPlayer().getAlreadyPlacedADie())
                        throw new AlreadyPlacedADieException("You already placed a die");
                    System.out.println(localCopyOfTheStatus.getCurrentPlayer());
                    System.out.println("Enter where you want to place your die ");
                    System.out.println("Row Index [0 - 3]");
                    int rowIndex = waitForUserInput(0, 3);
                    System.out.println("Col Index [0 - 4]");
                    int colIndex = waitForUserInput(0, 4);
                    if(localCopyOfTheStatus.getCurrentPlayer().getPlacedDice()[rowIndex][colIndex] != null)
                        throw new PositionOccupiedException("A die has already been placed here");
                    Die choseDie = choose(localCopyOfTheStatus.getDraftPool().toArray(new Die[localCopyOfTheStatus.getDraftPool().size()]));
                    System.out.println(choseDie);
                    controller.placeDie(ownerNameOfTheView, choseDie, rowIndex, colIndex);
                }
                catch(AlreadyPlacedADieException e){
                    System.out.println(e.getMessage());
                }
                catch(PositionOccupiedException e){
                    System.out.println(e.getMessage());
                }
                catch(PatternConstraintViolatedException e){
                    System.out.println(e.getMessage());
                }
                catch(RuleViolatedException e){
                    System.out.println(e.getMessage());
                }
                catch(IncompatibleMoveException e){
                    System.out.println(e.getMessage());
                }

            }
            else if (cmd == 2) {
                System.out.println("Choose a toolcard: ");
                controller.playToolCard(ownerNameOfTheView, choose(localCopyOfTheStatus.getToolCards().toArray(new ToolCard[localCopyOfTheStatus.getToolCards().size()])));

            }
            else {
                controller.endTurn();
            }
        }
        while(cmd != 3);
    }

    private boolean chooseFace(WindowPatternCard patternCard){
        int userInput;

        System.out.println(ownerNameOfTheView + ", now choose your pattern: ");
        System.out.println("1 -\n" + patternCard.getFrontPattern());
        System.out.println("2 -\n" + patternCard.getRearPattern());
        userInput = waitForUserInput(1,2);
        if(userInput == 1)
            return false;
        return true;


    }

    private int waitForUserInput(int lowerBound, int upperBound){
        int ret = 0;
        Scanner input = new Scanner(System.in);

        do{
            try{
                ret = input.nextInt();
            }
            catch(InputMismatchException e){
                System.out.println("Hai inserito un valore errato");
                ret = upperBound + 1;
                input.next();
            }
        }
        while(ret < lowerBound || ret > upperBound);
        return ret;
    }


    public Pair<WindowPatternCard, Boolean> choose(WindowPatternCard card1, WindowPatternCard card2) {
        WindowPatternCard cardChosen;
        Boolean faceChosen;
        int userInput;

        System.out.println("Enter: \n1 - " + card1 + "\n2 - " + card2);
        userInput = waitForUserInput(1,2);
        if(userInput == 1)
            cardChosen = card1;
        else
            cardChosen = card2;

        System.out.println("Enter: \n1 - " + cardChosen.getFrontPattern() + "\n2 - " + cardChosen.getRearPattern());
        userInput = waitForUserInput(1,2);
        if(userInput == 1)
            faceChosen = false;
        else
            faceChosen = true;

        return new Pair<WindowPatternCard, Boolean>(cardChosen, faceChosen);
    }


    public Die choose(Die[] diceList) {
        int ret = 0;

        System.out.println("Enter: ");
        for(int i = 0; i < diceList.length; i++){
            System.out.println(i + " - " + diceList[i]);
        }
        ret = waitForUserInput(0, diceList.length - 1);

        return diceList[ret];
    }


    public ToolCard choose(ToolCard[] toolCardList) {
        int ret = 0;

        System.out.println("Enter: ");
        for(int i = 0; i < toolCardList.length; i++) {
            System.out.println(i + " - " + toolCardList[i]);
        }
        ret = waitForUserInput(0,toolCardList.length - 1);
        return toolCardList[ret];
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
}
