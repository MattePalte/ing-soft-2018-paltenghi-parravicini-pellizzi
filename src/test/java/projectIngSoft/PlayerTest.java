package projectIngSoft;


import org.junit.*;
import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.Objectives.Privates.SfumatureBlu;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.View.LocalViewCli;
import projectIngSoft.exceptions.PatternConstraintViolatedException;
import projectIngSoft.exceptions.PositionOccupiedException;
import projectIngSoft.exceptions.RuleViolatedException;

import java.io.File;
import java.util.*;

public class PlayerTest {

    private Player testPlayer;
    private Player testPlayerWithWhitePatternCardNoMove;

    @Before
    public void playerCreation() {
        testPlayer = new Player("giocatore 1", new LocalViewCli("giocatore 1"));
        // set private objective
        testPlayer.setPrivateObjective(new SfumatureBlu());
        // set WindowPatternCard from file
        File file = new File("src/main/patterns.txt");
        Scanner input = null;
        try {
            input = new Scanner(file);
            WindowPatternCard window = WindowPatternCard.loadFromScanner(input);
            Assert.assertEquals("Kaleidoscopic Dream", window.getFrontPattern().getTitle());
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

        Player p = new Player(testPlayer);
        try {

            System.out.println(p );
            p .placeDieWithoutConstraints(new Die(3, Colour.RED),1,1);
            System.out.println(p );
            p .placeDieWithoutConstraints(new Die(3, Colour.GREEN),1,1);
            System.out.println(p );
            p .placeDieWithoutConstraints(new Die(3, Colour.BLUE),1,1);
            System.out.println(p );
            p .placeDieWithoutConstraints(new Die(3, Colour.VIOLET),1,1);
            System.out.println(p );
            p .placeDieWithoutConstraints(new Die(3, Colour.YELLOW),1,1);
            System.out.println(p );
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
        Player p = new Player(testPlayer);
        // retrieve rep
        Die[][] myMatrix = p.getPlacedDice();
        //Trying to modify internal rep
        myMatrix[0][0] = new Die(5, Colour.BLUE);
        // Debug ->
        /*System.out.println("Externally Modified Matrix -> \n");
        System.out.println(Arrays.deepToString(myMatrix));
        System.out.println("getPlacedDice  -> \n");
        System.out.println(Arrays.deepToString(testPlayer.getPlacedDice()));
        */
        Assert.assertFalse( Arrays.deepEquals(myMatrix, p.getPlacedDice()));
    }

    @Test
    // test correctness of positioning during place die
    public void placeDiePositioningTest() throws Exception {
        Player p = new Player(testPlayer);

        Die[][] initialMatrix = p.getPlacedDice();
        Die aDieToPlace = new Die(5, Colour.YELLOW);
        int modifiedRow = 0;
        int modifiedCol = 0;
        p.placeDie(aDieToPlace, modifiedRow, modifiedCol);

        Die[][] finalMatrix = p.getPlacedDice();
        // check that only that die has changed
        int nrRow = p.getPattern().getHeight();
        int nrCol = p.getPattern().getWidth();
        for (int i = 0 ; i < nrRow; i++){
            for (int j = 0 ; j < nrCol; j++){
                if (!(i == modifiedCol && j == modifiedRow)) {
                    if (initialMatrix[i][j] == null) {
                        Assert.assertTrue(initialMatrix[i][j] == null && finalMatrix[i][j] == null);
                    } else {
                        Assert.assertEquals(initialMatrix[i][j],(finalMatrix[i][j]));
                    }
                }
            }
        }
        Assert.assertEquals(finalMatrix[modifiedRow][modifiedCol],(aDieToPlace));


    }

    @Test
    // Since the first die must be placed on a corner / edge
    // This test ensure that when someone tries to put a die not onto the edge gets an error.
    //during test player is copied in order for placeDie being always the first move of the player
    public void testErroneousFirstPlayerMove() {
        Player p;
        Die aDie = new Die(Colour.BLUE).rollDie();
        boolean err;

        for (int row = 1; row < testPlayerWithWhitePatternCardNoMove.getPattern().getHeight() - 1; row++) {
            for (int col = 1; col < testPlayerWithWhitePatternCardNoMove.getPattern().getWidth() - 1; col++) {
                p = new Player(testPlayerWithWhitePatternCardNoMove);
                err = false;
                try {
                    p.placeDie(aDie, row, col);
                } catch (RuleViolatedException e) {
                    err = true;
                } catch (Exception ignored) {

                }
                Assert.assertTrue(err);
            }
        }
    }

    @Test
    // Since test first die must be placed on a corner / edge
    // This test ensures that when someone tries to put a die onto the edges, i.e the correct way of starting a game
    // no Exception is thrown.
    //during test player is copied in order for placeDie being always the first move of the player
    public void testNoErroneousFirstPlayerMove() {
        Player p;
        Die aDie = new Die(Colour.BLUE).rollDie();
        boolean err;

        for (int row = 0; row < testPlayerWithWhitePatternCardNoMove.getPattern().getHeight(); row++) {
            for (int col = 0; col < testPlayerWithWhitePatternCardNoMove.getPattern().getWidth(); col++) {
                if(col != 0 && col != testPlayerWithWhitePatternCardNoMove.getPattern().getWidth()-1 &&
                   row != 0 && row != testPlayerWithWhitePatternCardNoMove.getPattern().getHeight() -1) {
                    continue;
                }

                p = new Player(testPlayerWithWhitePatternCardNoMove);
                err = false;
                try {
                    p.placeDie(aDie, row, 0);
                } catch (RuleViolatedException e) {
                    err = true;
                } catch (Exception ignored) {

                }
                Assert.assertFalse(err);

            }
        }
    }

    @Test
    //This test ensure that a diePlaced made according to constraints can't raise PatternConstraintViolatedException.
    //I regret to not end up with a test of more usefulness. This is related to the fact that this test doesn't cover all
    //the possible placement of dice in the pattern card but proceeds with a naive fashion, trying to add randomly the dice.
    //excluding orthogonal rule and the presence of a die in adjacent positions (both fall into a RuleViolatedException) which would result in a valid move
    //the operation of placing a die couldn't raise a PatternConstraintViolatedException
    public void testPlaceDieNotViolatingConstraint(){
        Player p ;
        boolean err;

            for (int row = 0; row < testPlayer.getPattern().getHeight(); row++) {
                for (int col = 0; col < testPlayer.getPattern().getWidth(); col++) {
                    p = new Player(testPlayer);
                    err = false;
                    //if there's a color constraint in position row,col

                    //
                    Constraint aConstraint = p.getPattern().getConstraintsMatrix()[row][col];
                    if( Colour.validColours().contains(aConstraint.getColour())) {
                        try {
                            p.placeDie((new Die(aConstraint.getColour())).rollDie(), row, col);
                        } catch (PatternConstraintViolatedException e) {
                            err = true;
                        } catch (Exception ignored) {

                        }
                        Assert.assertFalse(err);
                    }else if( aConstraint.getValue() > 0){
                        try {
                            Colour aRandomColour = Colour.validColours().get( new Random().nextInt(Colour.validColours().size()));
                            p.placeDie((new Die(aConstraint.getValue(),aRandomColour )), row, col);
                        } catch (PatternConstraintViolatedException e) {
                            err = true;
                        } catch (Exception ignored) {

                        }
                        Assert.assertFalse(err);
                    }



                }
            }


    }

    @Test
    //This test tries with a recursive fashion to complete the windowPattern
    //since the dice are build up from scratch considering constraints limitation the placeDieMethod do not raise a PatternConstraintsViolatedException
    public void tryToCompleteTheWindow(){
        testPlayer.resetDieFlag();
        Player res = tryToCompleteTheWindow(testPlayer, 0, 0);
        System.out.println("Schema completed:"+res);

    }

    private Player tryToCompleteTheWindow(Player p, int row, int col) {
        Player localPlayer;
        Player result;

        Constraint aConstraint = p.getPattern().getConstraintsMatrix()[row][col];
        ArrayList<Die> compatiblesWithConstraint = buildDiceAccordingTo(aConstraint);

        for( Die aDieCompatibleWithConstraint : compatiblesWithConstraint ) {
            localPlayer = new Player(p);

            try {
                localPlayer.placeDie(aDieCompatibleWithConstraint, row, col);
            } catch (PatternConstraintViolatedException e) {
                Assert.fail(e.getMessage());
            } catch (Exception ignored) {
                continue;
            }

            localPlayer.resetDieFlag();


            if(row == localPlayer.getPattern().getHeight()-1 && col == localPlayer.getPattern().getWidth()-1 )
                return localPlayer;
            else if(col == p.getPattern().getWidth()-1 ){
                result = tryToCompleteTheWindow( new Player(localPlayer), row+1, 0);
            }else{
                result = tryToCompleteTheWindow( new Player(localPlayer), row, col+1);
            }

            if(result != null)
                return result;
        }
        return  null;
    }

    private ArrayList<Die> buildDiceAccordingTo(Constraint aConstraint){
        ArrayList<Die> ret = new ArrayList<>();
        for(Colour aColour : Colour.validColours()){
            if(aConstraint.getColour() != Colour.WHITE && aColour != aConstraint.getColour())
                continue;

            for (int i = 1; i <= 6; i++) {

                if(aConstraint.getValue() > 0 && i != aConstraint.getValue() )
                    continue;

                ret.add(new Die(i, aColour));
            }
        }
        return ret;
    }

    private ArrayList<Die> buildDiceAccordingTo(Die aDie){
        ArrayList<Die> ret = new ArrayList<>();
        for(Colour aColour : Colour.validColours()){
            if( aDie.getColour() != Colour.WHITE && aColour != aDie.getColour() )
                continue;

            for (int i = 1; i <= 6; i++) {

                if( aDie.getValue() > 0 && i != aDie.getValue())
                    continue;

                ret.add(new Die(i, aColour));
            }
        }
        return ret;
    }

    private ArrayList<Die> AllDice(){
        ArrayList<Die> ret = new ArrayList<>();
        for(Colour aColour : Colour.validColours()){
            for (int i = 1; i <= 6 ; i++) {
                ret.add(new Die(i, aColour));
            }
        }
        return ret;
    }

    private ArrayList<Die> buildDiceDisappointing(Die aDie){
        ArrayList<Die> ret = AllDice();
        ret.removeAll(buildDiceAccordingTo(aDie));
        return ret;
    }

    @Test
    //This test ensures that putting two dice of the same value, different colour in the window sharing an edge results in a error
    public void testPlacementBreakingValueAdjacencyRules(){


        for (int i = 0; i <= 6; i++) {
            ArrayList otherColors = new ArrayList<>(Colour.validColours());
            Player p = new Player(testPlayerWithWhitePatternCardNoMove);
            Colour aRandomColour1 = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));
            try {
                p.placeDie((new Die(i,aRandomColour1 )), 0,0);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }
            p.resetDieFlag();
            Colour aRandomColour2 = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));


            try {
                p.placeDie((new Die(i,aRandomColour2 )), 1,0);
                Assert.fail("A RuleViolatedException should be generated instead");
            } catch(RuleViolatedException ex){
                System.out.println("ok");
            } catch(Exception ignore){

            }
        }



    }

    @Test
    //This test ensures that putting two dice different values, same colour in the window sharing an edge results in a error
    public void testPlacementBreakingColourAdjacencyRules(){


        for (int i = 0; i <= 6; i++) {
            ArrayList otherColors = new ArrayList<>(Colour.validColours());
            Player p = new Player(testPlayerWithWhitePatternCardNoMove);
            Colour aRandomColour = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));
            try {
                p.placeDie((new Die(i,aRandomColour )), 0,0);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }
            p.resetDieFlag();


            try {
                p.placeDie((new Die(i,aRandomColour )), 1,0);
                Assert.fail("A RuleViolatedException should be generated instead");
            } catch(RuleViolatedException ex){
                System.out.println("ok");
            }  catch(Exception ex){
                Assert.fail(ex.getMessage());
            }
        }



    }

    @Test
    public void testTwoDieSameTurn(){
        for (int i = 0; i <= 6; i++) {
            ArrayList otherColors = new ArrayList<>(Colour.validColours());
            Player p = new Player(testPlayerWithWhitePatternCardNoMove);
            Colour aRandomColour1 = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));
            Die aDie = new Die(i,aRandomColour1 );

            try {
                p.placeDie( aDie, 0,0);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }

            ArrayList<Die> dice = buildDiceAccordingTo(aDie);
            Collections.shuffle(dice);
            try {
                p.placeDie(dice.get(0), 1,0);
                Assert.fail("A RuleViolatedException should be generated instead");
            } catch(RuleViolatedException ex){
                System.out.println("ok");
            } catch(Exception ex){
                Assert.fail(ex.getMessage());
            }
        }

    }


    @Test
    public void testTwoDieSameCoordinate(){
        for (int i = 0; i <= 6; i++) {
            ArrayList otherColors = new ArrayList<>(Colour.validColours());
            Player p = new Player(testPlayerWithWhitePatternCardNoMove);
            Colour aRandomColour1 = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));
            Die aDie = new Die(i,aRandomColour1 );

            try {
                p.placeDie( aDie, 0,0);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }

            p.resetDieFlag();

            ArrayList<Die> dice = buildDiceAccordingTo(aDie);
            Collections.shuffle(dice);
            try {
                p.placeDie(dice.get(0), 0,0);
                Assert.fail("A PositionOccupiedException should be generated instead");
            } catch(PositionOccupiedException ex){
                System.out.println("ok");
            } catch(Exception ex){
                Assert.fail(ex.getMessage());
            }
        }

    }





}