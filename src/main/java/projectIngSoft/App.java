package projectIngSoft;

import projectIngSoft.PrivateObjectiveImpl.SfumatureBlu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;


public class App 
{
    public static void main( String[] args ) throws FileNotFoundException, Colour.ColorNotFoundException {

        //TODO Capire come mai il privateObjective non riesce a leggere la sua descrizione (ritorna null) problemi con classe astratta e metodi ereditati??
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
            theGame.setupPhase();
        } else {
            System.out.print("Invalid Game created... \n");
        }

        /*
        // OLD CODE
        File file = new File("src/main/test.txt");
        WindowPattern window = new WindowPattern(new Scanner(file));

        System.out.println(window);

        Die myDie = new Die(0, Colour.RED);
        //System.out.println(myDie);

        ArrayList<Die> diceBag = new ArrayList<Die>();
        for (Colour c : Colour.validColours()){
            Die newDie = new Die(c);
            for(int i = 1; i <= 18; i++){

                diceBag.add(newDie.rollDie());
            }
        }
        System.out.println(diceBag);
        */
    }

    public static Game defaultGame(){
        Game theGame = new Game(3);
        theGame.add(new Player("Matteo"));
        theGame.add(new Player("Daniele"));
        theGame.add(new Player("Kris"));
        return theGame;
    }

    public static Game createGame() throws InputMismatchException{
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
