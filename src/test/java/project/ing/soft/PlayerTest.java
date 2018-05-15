package project.ing.soft;


import project.ing.soft.model.Colour;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.view.LocalViewCli;
import project.ing.soft.exceptions.PatternConstraintViolatedException;
import project.ing.soft.exceptions.PositionOccupiedException;
import project.ing.soft.exceptions.RuleViolatedException;
import org.junit.*;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.objectives.privates.SfumatureBlu;

import java.io.File;
import java.rmi.RemoteException;
import java.util.*;

public class PlayerTest {

    private Player testPlayer;
    private Player testPlayerWithWhitePatternCardNoMove;

    @Before
    public void playerCreation() throws RemoteException{
        testPlayer = new Player("giocatore 1", new LocalViewCli("giocatore 1"));
        // set private objective
        testPlayer.setPrivateObjective(new SfumatureBlu());
        // set WindowPatternCard from file
        File file = new File("src/main/resources/patterns.txt");
        Scanner input = null;
        try {
            input = new Scanner(file);
            WindowPatternCard window = WindowPatternCard.loadAPatternCardFromScanner(input);
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
        file = new File("src/main/resources/empty_pattern.txt");
        try {
            input = new Scanner(file);
            WindowPatternCard window = WindowPatternCard.loadAPatternCardFromScanner(input);

            testPlayerWithWhitePatternCardNoMove.setPatternCard(window);
        } catch (Exception e) {
            e.printStackTrace();
            if(input != null)
                input.close();

        }
    }

    //test every set_enable method
    @Test
    public void testMemento() throws PositionOccupiedException, RuleViolatedException, PatternConstraintViolatedException {

       Player memento = testPlayerWithWhitePatternCardNoMove.getMemento();

            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(1, Colour.BLUE), 0, 0, true);
            testPlayerWithWhitePatternCardNoMove.moveDice(List.of(new Coordinate(0,0)),List.of(new Coordinate(1,0)) , false, false, false);
            testPlayerWithWhitePatternCardNoMove.endTurn();
            Assert.assertNotEquals(testPlayerWithWhitePatternCardNoMove,memento);
            testPlayerWithWhitePatternCardNoMove.saveMemento(memento);
            Assert.assertEquals( testPlayerWithWhitePatternCardNoMove, memento);


    }

    @Test
    public void testTotring(){

        Player p = new Player(testPlayer);
        try {

            System.out.println(p );
            p .placeDie(new Die(2, Colour.YELLOW),0,0, true);
            p.endTurn();
            System.out.println(p );
            p .placeDie(new Die(3, Colour.BLUE),0,1, true);
            p.endTurn();
            System.out.println(p );
            p .placeDie(new Die(4, Colour.RED),0,2, true);
            p.endTurn();
            System.out.println(p );
            p .placeDie(new Die(5, Colour.GREEN),0,3, true);
            p.endTurn();
            System.out.println(p );
            p .placeDie(new Die(1, Colour.VIOLET),0,4, true);
            p.endTurn();
            System.out.println(p );
            p .placeDie(new Die(6, Colour.GREEN),1,0, true);
            p.endTurn();


            System.out.println(p );
        } catch (Exception e) {
           Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPlacementOnTheSamePositionException() {
        try {

            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(2, Colour.BLUE), 3, 4, true);
        }catch (Exception ex){
            Assert.fail();
        }
        testPlayerWithWhitePatternCardNoMove.endTurn();
        try {
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(4, Colour.VIOLET), 3, 4, true);
            Assert.fail();
        }catch (PositionOccupiedException ignored){

        }catch (Exception ex){
            Assert.fail();
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
        p.placeDie(aDieToPlace, modifiedRow, modifiedCol, true);

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
                    p.placeDie(aDie, row, col, true);
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
                    p.placeDie(aDie, row, 0, true);
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


                    Constraint aConstraint = testPlayer.getPattern().getConstraintsMatrix()[row][col];
                    for(Die aCompatibleDie: buildDiceAccordingTo(aConstraint)){
                        p = new Player(testPlayer);
                        err = false;
                        try {
                            p.placeDie( aCompatibleDie, row, col, true);
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
    //Test that force player to raise errors
    public void testPlaceDieViolatingConstraint(){
        Player p ;
        boolean err;

        for (int row = 0; row < testPlayer.getPattern().getHeight(); row++) {
            for (int col = 0; col < testPlayer.getPattern().getWidth(); col++) {
                //to simplify test use only edge of the WindowPattern
                if(col != 0 && col != testPlayerWithWhitePatternCardNoMove.getPattern().getWidth()-1 &&
                        row != 0 && row != testPlayerWithWhitePatternCardNoMove.getPattern().getHeight() -1) {
                    continue;
                }

                Constraint aConstraint = testPlayer.getPattern().getConstraintsMatrix()[row][col];
                for(Die aCompatibleDie: buildDiceDisappointing(aConstraint)){
                    p = new Player(testPlayer);
                    err = false;
                    try {
                        p.placeDie( aCompatibleDie, row, col, true);
                    } catch (PatternConstraintViolatedException e) {
                        err = true;
                    } catch (Exception ignored) {

                    }
                    Assert.assertTrue(err);

                }

            }
        }


    }

    @Test
    //This test tries with a recursive fashion to complete the windowPattern
    //since the dice are build up from scratch considering constraints limitation the placeDieMethod do not raise a PatternConstraintsViolatedException
    public void tryToCompleteTheWindow(){

        for(Die aCompatibleDie : buildDiceAccordingTo(testPlayer.getPattern().getConstraintsMatrix()[testPlayer.getPattern().getHeight()-1 ][testPlayer.getPattern().getWidth()-1 ])){
            Player p = new Player(testPlayer);

            /*try {
                p.placeDie(aCompatibleDie, p.getPattern().getHeight()-1, p.getPattern().getWidth()-1 );
                p.endTurn();
            } catch (Exception e){
                Assert.fail("cannot place the first die in the last corner ");
            }*/

            Player res = tryToCompleteTheWindow(p, 0, 0);
            System.out.println("Schema completed:"+res);
        }


    }

    private Player tryToCompleteTheWindow(Player p, int row, int col) {
        Player localPlayer ;
        Player result = null;

        if(p.getPlacedDice()[row][col] == null) {
            Constraint aConstraint = p.getPattern().getConstraintsMatrix()[row][col];
            ArrayList<Die> compatiblesWithConstraint = buildDiceAccordingTo(aConstraint);

            for (Die aDieCompatibleWithConstraint : compatiblesWithConstraint) {
                localPlayer = new Player(p);

                try {
                    localPlayer.placeDie(aDieCompatibleWithConstraint, row, col, true);
                } catch (PatternConstraintViolatedException e) {
                    Assert.fail(e.getMessage());
                } catch (Exception ignored) {
                    continue;
                }

                //System.out.println(localPlayer);
                localPlayer.endTurn();


                if (row == localPlayer.getPattern().getHeight() - 1 && col == localPlayer.getPattern().getWidth() - 1)
                    return localPlayer;
                else if (col == p.getPattern().getWidth() - 1) {
                    result = tryToCompleteTheWindow(new Player(localPlayer), row + 1, 0);
                } else {
                    result = tryToCompleteTheWindow(new Player(localPlayer), row, col + 1);
                }

                if(result != null)
                    return result;

            }
        }else{
                if (row == p.getPattern().getHeight() - 1 && col == p.getPattern().getWidth() - 1)
                    return p;
                else if (col == p.getPattern().getWidth() - 1) {
                    result = tryToCompleteTheWindow(new Player(p), row + 1, 0);
                } else {
                    result = tryToCompleteTheWindow(new Player(p), row, col + 1);
                }

        }


        return result;

    }

    @Test
    //This test tries with a recursive fashion to complete the windowPattern
    //since the dice are build up from scratch considering constraints limitation the placeDieMethod do not raise a PatternConstraintsViolatedException
    public void tryToCompleteTheWindowRandomly(){


            System.out.println(testPlayerWithWhitePatternCardNoMove);
            Player res = tryToCompleteTheWindowRandomlyPlaying(testPlayerWithWhitePatternCardNoMove);
            if(res != null) {
                System.out.println("Schema completed:" + res);
            }else
                Assert.fail("no solution was find to this pattern"+testPlayerWithWhitePatternCardNoMove);



    }

    private Player tryToCompleteTheWindowRandomlyPlaying(Player p) {
        Player localPlayer ;
        Player result = null;

        if(p.getEmptyCells() == 0) {
            return p;
        }else{

            List<Die> dice = buildAllDice();
            Collections.shuffle(dice);
            for (Die aDie : dice) {
                for (Coordinate c : p.getCompatiblePositions(aDie)) {
                    System.out.println(p);
                    System.out.println("Trying with "+aDie +" in position "+ c);
                    localPlayer = new Player(p);
                    try {
                        localPlayer.placeDie(aDie, c.getRow(), c.getCol(), true);
                    } catch (Exception ex) {
                        Assert.fail();
                    }
                    localPlayer.endTurn();
                    result = tryToCompleteTheWindowRandomlyPlaying(localPlayer);
                    if (result != null)
                        return result;
                }
            }
            return null;
        }
    }


    private ArrayList<Die> buildDiceAccordingTo(Constraint aConstraint){
        ArrayList<Die> ret = new ArrayList<>();
        for(Colour aColour : Colour.validColours()){
            //Dice that are said to be compatible with a Constraint have
            //the same color/value shown in the constraint
            //or if the constraint represent a blankspace aConstraint.getColour() == Colour.WHITE && aConstraint.getValue() == 0
            //a random die would be fine
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
            //dice are said to be compatible if they have both different colour AND value
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

    private ArrayList<Die> buildAllDice(){
        ArrayList<Die> ret = new ArrayList<>();
        for(Colour aColour : Colour.validColours()){
            for (int i = 1; i <= 6 ; i++) {
                ret.add(new Die(i, aColour));
            }
        }
        return ret;
    }

    private ArrayList<Die> buildDiceDisappointing(Die aDie){
        ArrayList<Die> ret = buildAllDice();
        ret.removeAll(buildDiceAccordingTo(aDie));
        return ret;
    }

    private ArrayList<Die> buildDiceDisappointing(Constraint aContraint){
        ArrayList<Die> ret = buildAllDice();
        ret.removeAll(buildDiceAccordingTo(aContraint));
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
                p.placeDie((new Die(i,aRandomColour1 )), 0,0, true);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }
            p.endTurn();
            Colour aRandomColour2 = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));


            try {
                p.placeDie((new Die(i,aRandomColour2 )), 1,0, true);
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
                p.placeDie((new Die(i,aRandomColour )), 0,0, true);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }
            p.endTurn();


            try {
                p.placeDie((new Die(i,aRandomColour )), 1,0, true);
                Assert.fail("A RuleViolatedException should be generated instead");
            } catch(RuleViolatedException ex){
                System.out.println("ok");
            }  catch(Exception ex){
                Assert.fail(ex.getMessage());
            }
        }



    }

    @Test
    //Ensures that when 2 dice are put in the same turn an exception is raised
    public void testTwoDieSameTurn(){
        for (int i = 0; i <= 6; i++) {
            ArrayList otherColors = new ArrayList<>(Colour.validColours());
            Player p = new Player(testPlayerWithWhitePatternCardNoMove);
            Colour aRandomColour1 = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));
            Die aDie = new Die(i,aRandomColour1 );

            try {
                p.placeDie( aDie, 0,0, true);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }

            ArrayList<Die> dice = buildDiceAccordingTo(aDie);
            Collections.shuffle(dice);
            try {
                p.placeDie(dice.get(0), 1,0, true);
                Assert.fail("A RuleViolatedException should be generated instead");
            } catch(RuleViolatedException ex){
                System.out.println("ok");
            } catch(Exception ex){
                Assert.fail(ex.getMessage());
            }
        }

    }

    @Test
    //Test that an exception is raised when 2 dice are put on the same coordinates
    public void testTwoDieSameCoordinate(){
        for (int i = 0; i <= 6; i++) {
            ArrayList otherColors = new ArrayList<>(Colour.validColours());
            Player p = new Player(testPlayerWithWhitePatternCardNoMove);
            Colour aRandomColour1 = (Colour) otherColors.remove( new Random().nextInt(otherColors.size()));
            Die aDie = new Die(i,aRandomColour1 );

            try {
                p.placeDie( aDie, 0,0, true);
            } catch (Exception ex){
                Assert.fail(ex.getMessage());
            }

            p.endTurn();

            ArrayList<Die> dice = buildDiceAccordingTo(aDie);
            Collections.shuffle(dice);
            try {
                p.placeDie(dice.get(0), 0,0, true);
                Assert.fail("A PositionOccupiedException should be generated instead");
            } catch(PositionOccupiedException ex){
                System.out.println("ok");
            } catch(Exception ex){
                Assert.fail(ex.getMessage());
            }
        }

    }

    @Test
    //Test that an exception is thrown when you're trying to put a die in places that are away from
    //other previous put dice
    public void testDistanceControl(){
        Player p, p1;
        Random rnd = new Random();
        boolean err;

        Die aDie = new Die(Colour.validColours().get(rnd.nextInt(Colour.validColours().size()))).rollDie();
        int height = testPlayerWithWhitePatternCardNoMove.getPattern().getHeight();
        int width = testPlayerWithWhitePatternCardNoMove.getPattern().getWidth();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if(col != 0 && col != width -1 &&
                        row != 0 && row != height -1) {
                    continue;
                }

                p = new Player(testPlayerWithWhitePatternCardNoMove);

                try {
                    p.placeDie(aDie, row, col, true);
                } catch (Exception e) {
                    Assert.fail("Error");
                }

                p.endTurn();

                for (int otherRow = 0; otherRow < height; otherRow++) {
                    for (int otherCol = 0; otherCol < width; otherCol++) {
                        if (    otherRow > row + 1 || otherRow < row -1 ||
                                otherCol > col + 1 || otherCol < col -1) {
                            for (Die otherDie : buildDiceAccordingTo(aDie)) {
                                p1 = new Player(p);
                                err = false;
                                try {
                                    p1.placeDie(otherDie, otherRow, otherCol, true);
                                } catch (Exception ex) {
                                    err = true;
                                }
                                Assert.assertTrue(err);
                            }
                        }
                    }
                }

            }
        }



    }

    @Test
    // check adjacent placing
    public void testDieCornerToCorner(){

        try {
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(3, Colour.YELLOW), 3,3, true);
            testPlayerWithWhitePatternCardNoMove.endTurn();
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(6, Colour.BLUE), 2,4, true);
            testPlayerWithWhitePatternCardNoMove.endTurn();
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(2, Colour.VIOLET), 2,3, true);

        } catch (Exception e) {
           Assert.fail();
        }


    }

    @Test
    public void testMovementSwapping(){
        try {
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(2, Colour.VIOLET), 0, 0, true);
            testPlayerWithWhitePatternCardNoMove.endTurn();
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(4, Colour.RED), 0, 1, true);
            testPlayerWithWhitePatternCardNoMove.moveDice(List.of(new Coordinate(0,0), new Coordinate(0,1)),
                                                            List.of(new Coordinate(1,0), new Coordinate(0,0)), true, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMovementExceptionWhenOccupied(){
        try {
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(2, Colour.VIOLET), 2, 0, true);
            testPlayerWithWhitePatternCardNoMove.endTurn();
            testPlayerWithWhitePatternCardNoMove.placeDie(new Die(4, Colour.RED), 3, 1, true);
            testPlayerWithWhitePatternCardNoMove.moveDice(List.of(new Coordinate(2,0)),
                    List.of( new Coordinate(3,1)), true, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMovementExceptionWhenDieIsNull(){
        try {
            testPlayerWithWhitePatternCardNoMove.moveDice(List.of(new Coordinate(0,0)), List.of(new Coordinate(3,4)),true, true, true);
        } catch (RuleViolatedException ignored) {

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetEmptyCells(){
        Assert.assertEquals(testPlayer.getEmptyCells() ,
                testPlayer.getPattern().getHeight()*testPlayer.getPattern().getWidth());

        Assert.assertEquals(testPlayerWithWhitePatternCardNoMove.getEmptyCells() ,
                testPlayerWithWhitePatternCardNoMove.getPattern().getHeight()*testPlayerWithWhitePatternCardNoMove.getPattern().getWidth());
        Player result = tryToCompleteTheWindow(testPlayer, 0,0);
        Assert.assertEquals(0,result.getEmptyCells()) ;
    }

    //region serialization
    public void testSerialization(){
        //TODO add test about serialization
    }

    //endregion

}