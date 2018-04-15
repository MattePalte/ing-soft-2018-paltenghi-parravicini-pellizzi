package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Colour;
import projectIngSoft.Controller.Controller;
import projectIngSoft.Controller.IController;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.AlreadyPlacedADieException;
import projectIngSoft.exceptions.ConstraintViolatedException;
import projectIngSoft.exceptions.PositionOccupiedException;

import javax.swing.text.Position;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LocalViewCli implements IView{
    IGameManager gameStatus;
    IController controller;
    String ownerNameOfTheView;

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
    public void update(IGameManager newModel) {
        gameStatus = newModel;
        // TODO: abilitare stampa di tutti i giocatori su tutti i client con il codice sotto
        /*for (Player p : gameStatus.getPlayerList()) {
            System.out.println(p);
        }*/
        // Stampa solo situazione attuale del giocatore attuale
        for (Player p : gameStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                System.out.println(p);
            }
        }
        System.out.println("Draft pool : "+gameStatus.getDraftPool());
    }

    @Override
    public void endTurn() throws Exception {
        controller.endTurn();
    }

    @Override
    public void takeTurn() throws Exception {
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
                    controller.placeDie(choseDie, rowIndex, colIndex);
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

            }
            else if (cmd == 2) {
                System.out.println("Choose a toolcard: ");
                controller.playToolCard(choose(gameStatus.getToolCards().toArray(new ToolCard[gameStatus.getToolCards().size()])));

            }
            else {
                controller.endTurn();
            }
        }
        while(cmd != 3);
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
