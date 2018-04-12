package projectIngSoft;


import org.junit.*;
import projectIngSoft.Cards.Objectives.Privates.SfumatureBlu;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.View.LocalViewCli;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Scanner;

public class PlayerTest {

    private Player testPlayer;

    @Before
    public void playerCreation() {
        this.testPlayer = new Player("Matteo", new LocalViewCli());
        // set private objective
        testPlayer.setPrivateObjective(new SfumatureBlu());
        // set WindowPatternCard from file
        File file = new File("src/main/patterns.txt");
        try {
            Scanner input = new Scanner(file);
            WindowPatternCard window = WindowPatternCard.loadFromScanner(input);
            input.nextLine();
            testPlayer.setPatternCard(window);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTotring(){
        this.testPlayer = new Player("Matteo", new LocalViewCli());
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
            testPlayer.placeDie(new Die(3, Colour.RED),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDie(new Die(3, Colour.GREEN),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDie(new Die(3, Colour.BLUE),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDie(new Die(3, Colour.VIOLET),1,1);
            System.out.println(testPlayer);
            testPlayer.placeDie(new Die(3, Colour.YELLOW),1,1);
            System.out.println(testPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testColor() throws UnsupportedEncodingException {

        System.out.println("\033[32;1mgreen\033[0m");
    }


    @Test
    // to test the privacy of the rep
    // it test primarly cloneArray private method
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
    public void placeDieTest(){
        Die[][] initialMatrix = testPlayer.getPlacedDice();
        Die aDieToPlace = new Die(5, Colour.BLUE);
        int modifiedRow = 0;
        int modifiedCol = 0;
        testPlayer.placeDie(aDieToPlace, modifiedRow, modifiedCol);

        Die[][] finalMatrix = testPlayer.getPlacedDice();
        // check that only that die has changed
        int nrRow = testPlayer.getVisiblePattern().getHeight();
        int nrCol = testPlayer.getVisiblePattern().getWidth();
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

}