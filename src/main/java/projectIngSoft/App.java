package projectIngSoft;

import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.GameManager.GameManagerMulti;
import projectIngSoft.View.LocalViewCli;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class App 
{
    public static void main( String[] args ) throws Exception {

        Scanner scanner = new Scanner(System.in);
        Game theGame = createGame();

        /*if (theGame.isValid()) {
            IGameManager referee = new GameManagerMulti(theGame);

            referee.countPlayersPoints();
            Player p = referee.getWinner();
            System.out.println("Player "+ p +" wins!");
        } else {
            System.out.print("Invalid Game created... \n");
        }
*/


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
            Player newPlayer = new Player(newName, new LocalViewCli(newName));
            theGame.add(newPlayer);
        }
        return theGame;
    }
}
