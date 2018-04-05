package projectIngSoft;

import projectIngSoft.Cards.Objectives.Privates.SfumatureBlu;
import projectIngSoft.Referee.RefereeController;
import projectIngSoft.Referee.RefereeControllerMultiplayer;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class App 
{
    public static void main( String[] args ) throws Exception {

        System.out.print("descrtiption -> " + new SfumatureBlu().getDescription() + "\n");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Use default configuration? y/n ");
        String answer = scanner.next();
        Game theGame;
        if (answer.equals("y")) {
            theGame = defaultGame();
        } else {
            try {
                theGame = createGame();
            } catch (InputMismatchException e) {
                System.out.print("Mismatch Input... \n");
                return;
            }
        }
        if (theGame.isValid()) {
            RefereeController referee = new RefereeControllerMultiplayer(theGame);
            referee.startGame();
        } else {
            System.out.print("Invalid Game created... \n");
        }



    }

    public static Game defaultGame() throws FileNotFoundException, Colour.ColorNotFoundException {
        Game theGame = new Game(3);
        theGame.add(new Player("Matteo"));
        theGame.add(new Player("Daniele"));
        theGame.add(new Player("Kris"));
        return theGame;
    }

    public static Game createGame() throws InputMismatchException, FileNotFoundException, Colour.ColorNotFoundException {
        // create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);
        //  prompt for the number of players
        int nPlayers;
        System.out.print("Enter the number of players: ");
        nPlayers = scanner.nextInt();

        // create the game
        Game theGame = new Game(nPlayers);
        System.out.println("New Game successfully created!");

        // loop for each player
        for (int i = 1; i <= nPlayers; i++) {
            //  prompt for the player name
            System.out.println(String.format("Name for player nr %d : ", i));
            String newName = scanner.next();
            Player newPlayer = new Player(newName);
            theGame.add(newPlayer);
        }
        return theGame;
    }
}
