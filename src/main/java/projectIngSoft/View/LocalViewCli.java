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
    public void endTurn() {
        controller.endTurn();
    }

    @Override
    public void takeTurn() {
        Scanner input = new Scanner(System.in);
        int cmd;

        do {
            System.out.println("Take your turn:");
            System.out.println("1 - Place a die");
            System.out.println("2 - Play a toolcard");
            try {
                cmd = input.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(e.getStackTrace());
                cmd = 0;
            }
        }
        while (cmd != 1 && cmd != 2);

        // user selected to place a die
        if (cmd == 1) {
            // Select row index
            do {
                System.out.println(gameStatus.getCurrentPlayer().getPlacedDice());
                System.out.println("Enter where you want to place your die ");
                System.out.println("Row Index [0 - 3]");
                try {
                    cmd = input.nextInt();
                }
                catch (InputMismatchException e) {
                    System.out.println(e.getStackTrace());
                    cmd = 4;
                }
            }
            while(cmd < 0 || cmd > 3);
            int rowIndex = cmd;

            // Select col index
            do {
                System.out.println("Col Index [0 - 4]");
                try {
                    cmd = input.nextInt();
                }
                catch (InputMismatchException e) {
                    System.out.println(e.getStackTrace());
                    cmd = 5;
                }
            }
            while(cmd < 0 || cmd > 4);
            int colIndex = cmd;
            controller.placeDie(choose(gameStatus.getDraftPool().toArray(new Die[gameStatus.getDraftPool().size()])), rowIndex, colIndex);
        }

        else{
            System.out.println("Choose a toolcard: ");
            controller.playToolCard(choose(gameStatus.getToolCards().toArray(new ToolCard[gameStatus.getToolCards().size()])));
        }
    }

    @Override
    public Pair<WindowPatternCard, Boolean> choose(WindowPatternCard card1, WindowPatternCard card2) {
        Scanner input = new Scanner(System.in);
        WindowPatternCard cardChosen;
        Boolean faceChosen;
        int userInput;

        do {
            System.out.println("Enter: \n1 - " + card1 + "\n2 - " + card2);
            try{
                userInput = input.nextInt();
            }
            catch(InputMismatchException e){
                System.out.println(e.getStackTrace());
                userInput = 0;
            }
        }
        while(userInput != 1 && userInput != 2);
        if(userInput == 1)
            cardChosen = card1;
        else
            cardChosen = card2;

        do{
            System.out.println("Enter: \n1 - " + cardChosen.getFrontPattern() + "\n2 - " + cardChosen.getRearPattern());
            userInput = input.nextInt();
        }
        while(userInput != 1 && userInput != 2);
        if(userInput == 1)
            faceChosen = false;
        else
            faceChosen = true;

        return new Pair<WindowPatternCard, Boolean>(cardChosen, faceChosen);
    }

    @Override
    public Die choose(Die[] diceList) {
        int ret = 0;
        Scanner input = new Scanner(System.in);

        do{
            System.out.println("Enter: ");
            for(int i = 0; i < diceList.length; i++){
                System.out.println(i + " - " + diceList[i]);
                try {
                    ret = input.nextInt();

                }
                catch(InputMismatchException e){
                    System.out.println(e.getStackTrace());
                    ret = 0;
                }
            }
        }
        while(ret < 1 || ret > diceList.length);
        return diceList[ret];
    }

    @Override
    public ToolCard choose(ToolCard[] toolCardList) {
        int ret = 0;
        Scanner input = new Scanner(System.in);

        do{
            System.out.println("Enter: ");
            for(int i = 0; i < toolCardList.length; i++) {
                System.out.println(i + " - " + toolCardList[i]);
                try{
                    ret = input.nextInt();
                }
                catch(InputMismatchException e){
                    System.out.println(e.getStackTrace());
                    ret = 0;
                }
            }
        }
        while(ret < 1 || ret > toolCardList.length);
        return toolCardList[ret];
    }
}
