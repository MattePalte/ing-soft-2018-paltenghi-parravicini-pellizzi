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

        Scanner scanner = new Scanner(System.in);
        Game theGame = createGame();

        if (theGame.isValid()) {
            RefereeController referee = new RefereeControllerMultiplayer(theGame);
            referee.setupPhase();
            referee.watchTheGame();
            referee.attributePoints();
            Player p = referee.getWinner();
            System.out.println("Player "+ p +" wins!");
        } else {
            System.out.print("Invalid Game created... \n");
        }



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
