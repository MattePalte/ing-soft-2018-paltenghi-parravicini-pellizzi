package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.Controller;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class LocalViewCli implements IView{
    IGameManager gameStatus;
    Controller controller;

    @Override
    public void update(IGameManager newModel) {
        gameStatus = newModel;
    }

    @Override
    public void endTurn() throws Exception {
        controller.endTurn();
    }

    @Override
    public void takeTurn() throws Exception {
        int cmd;

        do {
            System.out.println("Take your turn:");
            System.out.println("1 - Place a die");
            System.out.println("2 - Play a toolcard");
            System.out.println("3 - End your turn");
            cmd = waitForUserInput(1,3);

            if (cmd == 1) {
                // Select row index

                System.out.println(gameStatus.getCurrentPlayer().getPlacedDice());
                System.out.println("Enter where you want to place your die ");
                System.out.println("Row Index [0 - 3]");
                int rowIndex =  waitForUserInput(0,3);
                System.out.println("Col Index [0 - 4]");
                int colIndex = waitForUserInput(0,4);
                controller.placeDie(choose(gameStatus.getDraftPool().toArray(new Die[gameStatus.getDraftPool().size()])), rowIndex, colIndex);

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

    private int waitForUserInput(int lowerBound, int upperBound){
        int ret = 0;
        Scanner input = new Scanner(System.in);

        do{
            try{
                ret = input.nextInt();
            }
            catch(InputMismatchException e){
                System.out.println(e.getStackTrace());
                ret = upperBound + 1;
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
