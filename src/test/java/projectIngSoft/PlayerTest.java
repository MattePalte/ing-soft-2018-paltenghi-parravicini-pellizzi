package projectIngSoft;


import org.junit.*;
import projectIngSoft.Cards.Objectives.Privates.SfumatureBlu;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.View.LocalViewCli;
import projectIngSoft.exceptions.PatternConstraintViolatedException;
import projectIngSoft.exceptions.PositionOccupiedException;
import projectIngSoft.exceptions.RuleViolatedException;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Scanner;

public class PlayerTest {

    private Player testPlayer;
    private Player testPlayerWithWhitePatternCardNoMove;

    @Before
    public void playerCreation() {
        testPlayer = new Player("Matteo", new LocalViewCli("Matteo"));
        // set private objective
        testPlayer.setPrivateObjective(new SfumatureBlu());
        // set WindowPatternCard from file
        File file = new File("src/main/patterns.txt");
        Scanner input = null;
        try {
            input = new Scanner(file);
            WindowPatternCard window = WindowPatternCard.loadFromScanner(input);

            testPlayer.setPatternCard(window);
        } catch (Exception e) {
            e.printStackTrace();
            if(input != null)
                input.close();

        }

        testPlayerWithWhitePatternCardNoMove = new Player("Test", new LocalViewCli("Test") );
        // set private objective
        testPlayerWithWhitePatternCardNoMove.setPrivateObjective(new SfumatureBlu());
        // set WindowPatternCard from file
        file = new File("src/main/empty_pattern.txt");
        try {
            input = new Scanner(file);
            WindowPatternCard window = WindowPatternCard.loadFromScanner(input);

            testPlayerWithWhitePatternCardNoMove.setPatternCard(window);
        } catch (Exception e) {
            e.printStackTrace();
            if(input != null)
                input.close();

        }
    }

    @Test
    public void testTotring(){
        this.testPlayer = new Player("Matteo", new LocalViewCli("Matteo"));
        // set private objective
        testPlayer.setPrivateObjective(new SfumatureBlu());
        // set WindowPatternCard from file
        File file = new File("src/main/patterns.txt");
        try {
            Scanner input = new Scanner(file);
            WindowPatternCard window = WindowPatternCard.loadFromScanner(input);
            input.nextLine();
            testPlayer.setPatternCard(window);
            System.out.println(testPlayer);
            testPlayer.placeDieWithoutConstraints(new Die(3, Colour.RED),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDieWithoutConstraints(new Die(3, Colour.GREEN),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDieWithoutConstraints(new Die(3, Colour.BLUE),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDieWithoutConstraints(new Die(3, Colour.VIOLET),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDieWithoutConstraints(new Die(3, Colour.YELLOW),1,1);
            System.out.println(testPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testColor() {

        System.out.println("\033[32;1mgreen\033[0m");
    }


    @Test
    // to test the privacy of the rep
    // it test primary cloneArray private method
    public void privacyOfMatrixRep(){
        // retrieve rep
        Die[][] myMatrix = testPlayer.getPlacedDice();
        //modify internal rep
        myMatrix[0][0] = new Die(5, Colour.BLUE);
        // Debug ->
        /*System.out.println("Externally Modified Matrix -> \n");
        System.out.println(Arrays.deepToString(myMatrix));
        System.out.println("getPlacedDice  -> \n");
        System.out.println(Arrays.deepToString(testPlayer.getPlacedDice()));
        */
        Assert.assertFalse( Arrays.deepEquals(myMatrix, testPlayer.getPlacedDice()));
    }

    @Test
    // test place die
    public void placeDieTest() throws Exception {
        Die[][] initialMatrix = testPlayer.getPlacedDice();
        Die aDieToPlace = new Die(5, Colour.BLUE);
        int modifiedRow = 0;
        int modifiedCol = 0;
        testPlayer.placeDie(aDieToPlace, modifiedRow, modifiedCol);

        Die[][] finalMatrix = testPlayer.getPlacedDice();
        // check that only that die has changed
        int nrRow = testPlayer.getPattern().getHeight();
        int nrCol = testPlayer.getPattern().getWidth();
        for (int i = 0 ; i < nrRow; i++){
            for (int j = 0 ; j < nrCol; j++){
                if (!(i == modifiedCol && j == modifiedRow)) {
                    if (initialMatrix[i][j] == null) {
                        Assert.assertTrue(initialMatrix[i][j] == null && finalMatrix[i][j] == null);
                    } else {
                        Assert.assertTrue(initialMatrix[i][j].equals(finalMatrix[i][j]));
                    }
                }
            }
        }
        Assert.assertTrue(finalMatrix[modifiedRow][modifiedCol].equals(aDieToPlace));


    }

    @Test
    // test first die must be placed on a corner / edge
    //during test player is copied in order for placeDie being always the first move of the player
    public void testFirstPlayerMove(){
        Player p ;
        Die aDie = new Die(Colour.BLUE).rollDie();
        boolean flag ;
        for (int row = 1; row < testPlayerWithWhitePatternCardNoMove.getPattern().getHeight()-1; row++) {
            for (int col = 1; col < testPlayerWithWhitePatternCardNoMove.getPattern().getWidth()-1; col++) {
                p = new Player(testPlayerWithWhitePatternCardNoMove);
                flag = false;
                try {
                    p.placeDie(aDie, row, col);
                }  catch (RuleViolatedException e) {
                   flag = true;
                } catch (Exception e){

                }
                Assert.assertTrue(flag);
            }
        }


    }

}