package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Colour;
import projectIngSoft.Controller.IController;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.events.*;
import projectIngSoft.exceptions.*;

import java.util.*;

public class LocalViewCli implements IView, IEventHandler {
    IGameManager gameStatus;
    IController controller;
    String ownerNameOfTheView;

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        System.out.println("Evento ricevuto da " + ownerNameOfTheView + " : Giocatore Cambiato");
        if (gameStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)){
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
        if (gameStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)){
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
        for (Player p : gameStatus.getPlayerList()) {
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

    public LocalViewCli(String ownerNameOfTheView) {
        // TODO: come fa una view a sapere chi è il suo "padrone" (player)?
        // getCurrentPlayer da solo il giocatore di turno non il giocatore della view
        this.ownerNameOfTheView = ownerNameOfTheView;
    }

    public LocalViewCli() {
        // inutile, presente solo per retrocompatibilità con test che usavano LocalView senza parametri
    }

    @Override
    public void attachController(IController aController){
        // TODO: come fa la view a sapere a che controller deve parlare?
        this.controller = aController;
    }

    @Override
    public void update(IGameManager newModel, Event event) {
        gameStatus = newModel;
        event.accept(this);
    }

    private void displayMySituation(){
        // Stampa solo situazione attuale del giocatore attuale
        for (Player p : gameStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                System.out.println(p);
            }
        }
        System.out.println("Draft pool : "+gameStatus.getDraftPool());
    }

    private void displayEntireGameBoard(){
        // TODO: abilitare stampa di tutti i giocatori su tutti i client con il codice sotto
        /*for (Player p : gameStatus.getPlayerList()) {
            System.out.println(p);
        }*/
        System.out.println("Draft pool : "+gameStatus.getDraftPool());
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
            System.out.println("Take your turn " + gameStatus.getCurrentPlayer().getName());
            System.out.println("1 - Place a die");
            System.out.println("2 - Play a toolcard");
            System.out.println("3 - End your turn");
            cmd = waitForUserInput(1,3);

            if (cmd == 1) {
                try {
                    if (gameStatus.getCurrentPlayer().getAlreadyPlacedADie())
                        throw new AlreadyPlacedADieException("You already placed a die");
                    System.out.println(gameStatus.getCurrentPlayer());
                    System.out.println("Enter where you want to place your die ");
                    System.out.println("Row Index [0 - 3]");
                    int rowIndex = waitForUserInput(0, 3);
                    System.out.println("Col Index [0 - 4]");
                    int colIndex = waitForUserInput(0, 4);
                    if(gameStatus.getCurrentPlayer().getPlacedDice()[rowIndex][colIndex] != null)
                        throw new PositionOccupiedException("A die has already been placed here");
                    Die choseDie = choose(gameStatus.getDraftPool().toArray(new Die[gameStatus.getDraftPool().size()]));
                    System.out.println(choseDie);
                    checkConstaints(rowIndex, colIndex, choseDie);
                    checkAdjacents(getAdjacents(gameStatus.getCurrentPlayer().getPlacedDice(), rowIndex, colIndex), choseDie);
                    checkAdjacentConstraints(getAdjacentConstraints(gameStatus.getCurrentPlayer().getPattern().getConstraintsMatrix(), rowIndex, colIndex), choseDie);
                    controller.placeDie(ownerNameOfTheView, choseDie, rowIndex, colIndex);
                }
                catch(AlreadyPlacedADieException e){
                    System.out.println(e.getMessage());
                }
                catch(PositionOccupiedException e){
                    System.out.println(e.getMessage());
                }
                catch(ConstraintViolatedException e){
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
                controller.playToolCard(ownerNameOfTheView, choose(gameStatus.getToolCards().toArray(new ToolCard[gameStatus.getToolCards().size()])));

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

    private void checkAdjacentConstraints(List<Constraint> adjacentConstraints, Die choseDie) throws IncompatibleMoveException {
        for (int i = 0; i < adjacentConstraints.size(); i++) {
            if (adjacentConstraints.get(i).getValue() == choseDie.getValue() || adjacentConstraints.get(i).getColour().equals(choseDie.getColour()))
                throw new IncompatibleMoveException("You can't place this die here: it's incompatible with adjacent constraints");
        }
    }

    private ArrayList<Constraint> getAdjacentConstraints(Constraint[][] constraints, int row, int col){
        ArrayList<Constraint> ret = new ArrayList<>();

        if(col + 1 < constraints[row].length)
            ret.add(constraints[row][col+1]);
        if(col > 0)
            ret.add(constraints[row][col-1]);
        if(row + 1 < constraints.length)
            ret.add(constraints[row+1][col]);
        if(row > 0)
            ret.add(constraints[row-1][col]);
        return ret;
    }

    private void checkAdjacents(List<Die> adjacents, Die choseDie) throws RuleViolatedException {
        for(int i = 0; i < adjacents.size(); i++){
            Die placedDie = adjacents.get(i);
            if(placedDie != null && (placedDie.getValue() == choseDie.getValue() || placedDie.getColour().equals(choseDie.getColour()))) {
                throw new RuleViolatedException("Ehi! You are trying to place a die with the same colour or the same value than an adjacent die. You can't do whatever you want! You must follow the rules");
            }
        }
    }

    private ArrayList<Die> getAdjacents(Die[][] placedDice, int row, int col){
        ArrayList<Die> ret = new ArrayList<>();

        if(col + 1 < placedDice[row].length)
            ret.add(placedDice[row][col+1]);
        if(col > 0)
            ret.add(placedDice[row][col-1]);
        if(row + 1 < placedDice.length)
            ret.add(placedDice[row+1][col]);
        if(row > 0)
            ret.add(placedDice[row-1][col]);

        return ret;
    }

    private void checkConstaints(int rowIndex, int colIndex, Die aDie) throws ConstraintViolatedException {
        Constraint actualConstraint = gameStatus.getCurrentPlayer().getPattern().getConstraintsMatrix()[rowIndex][colIndex];
        if((!actualConstraint.getColour().equals(aDie.getColour()) && !actualConstraint.getColour().equals(Colour.WHITE))|| (actualConstraint.getValue()!=aDie.getValue() && actualConstraint.getValue() != 0))
            throw new ConstraintViolatedException("Ehi, you cheater! You are violating a constraint on your pattern! Try again, and play fairly!");
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

    @Override
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

    @Override
    public Die choose(Die[] diceList) {
        int ret = 0;

        System.out.println("Enter: ");
        for(int i = 0; i < diceList.length; i++){
            System.out.println(i + " - " + diceList[i]);
        }
        ret = waitForUserInput(0, diceList.length - 1);

        return diceList[ret];
    }

    @Override
    public ToolCard choose(ToolCard[] toolCardList) {
        int ret = 0;

        System.out.println("Enter: ");
        for(int i = 0; i < toolCardList.length; i++) {
            System.out.println(i + " - " + toolCardList[i]);
        }
        ret = waitForUserInput(0,toolCardList.length - 1);
        return toolCardList[ret];
    }
}
