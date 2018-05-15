package project.ing.soft;

import org.junit.*;
import org.junit.rules.ExpectedException;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.PatternConstraintViolatedException;
import project.ing.soft.exceptions.PositionOccupiedException;
import project.ing.soft.exceptions.RuleViolatedException;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.model.gamemanager.GameManagerMulti;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.view.LocalViewCli;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ToolCardTest {
    private Player playerStub;
    private IGameManager gameManagerStub;
    private ArrayList<Die> draftPoolStub;
    private WindowPattern playerPatternStub;
    private RoundTracker roundTrackerStub;
    private ArrayList<Player> playerListStub;
    private ArrayList<Player> turnListStub;
    private ArrayList<Die> diceLeftStub;
    private Die[][] placedDiceStub;
    private Random rndGen;

    @Before
    public void testSetup(){
        playerStub = mock(Player.class);
        gameManagerStub = mock(GameManagerMulti.class);
        draftPoolStub = new ArrayList<Die>();
        playerPatternStub = mock(WindowPattern.class);
        placedDiceStub = new Die[4][5];
        roundTrackerStub = mock(RoundTracker.class);
        playerListStub = new ArrayList<Player>();
        turnListStub = new ArrayList<Player>();
        diceLeftStub = new ArrayList<Die>();
        rndGen = new Random();
        for(int i = 0; i < 5; i++){
            draftPoolStub.add(new Die(Colour.WHITE));
            diceLeftStub.add(new Die(Colour.WHITE));
        }

        // Provide a turnListStub with size less than playerStub.size()
        for(int i = 0; i < 4; i++){
            playerListStub.add(playerStub);
            if(i<3)
                turnListStub.add(playerStub);
        }

        when(gameManagerStub.getDraftPool()).thenReturn(draftPoolStub);
        when(playerStub.getPattern()).thenReturn(playerPatternStub);

        // when getHeight is called, return 4 as number of rows
        when(playerPatternStub.getHeight()).thenReturn(4);

        // when getWidth is called, return 5 as number of rows
        when(playerPatternStub.getWidth()).thenReturn(5);

        when(playerStub.getPlacedDice()).thenReturn(placedDiceStub);

        when(gameManagerStub.getRoundTracker()).thenReturn(roundTrackerStub);

        when(roundTrackerStub.getDiceLeftFromRound()).thenReturn(diceLeftStub);

        // When gameManagerStub.addToDraft(aDie) is called, add that die to the draftPoolStub instead
        doAnswer((invocation) -> {
            draftPoolStub.add(invocation.getArgument(0));
            return null;
        }).when(gameManagerStub).addToDraft(any(Die.class));

        doAnswer((invocation) -> {
            draftPoolStub.remove(invocation.getArgument(0));
            return null;
        }).when(gameManagerStub).removeFromDraft(any(Die.class));

        doAnswer((invocation) -> {
            diceLeftStub.remove(invocation.getArgument(1));
            diceLeftStub.add(invocation.getArgument(0));
            return true;
        }).when(roundTrackerStub).swapDie(any(Die.class), any(Die.class));

        doAnswer((invocation) -> {
            roundTrackerStub.swapDie(invocation.getArgument(0), invocation.getArgument(1));
            return true;
        }).when(gameManagerStub).swapWithRoundTracker(any(Die.class), any(Die.class));

        doAnswer((invocation) -> {
            for(int i = 0; i < draftPoolStub.size(); i++){
                draftPoolStub.add(i, draftPoolStub.remove(i).rollDie());
            }
            return true;
        }).when(gameManagerStub).rollDraftPool();

        when(gameManagerStub.getCurrentTurnList()).thenReturn(turnListStub);

        when(gameManagerStub.getPlayerList()).thenReturn(playerListStub);

        doAnswer((invocation) -> {
            Player current = turnListStub.get(0);
            return current;
        }).when(gameManagerStub).getCurrentPlayer();

        doAnswer((invocation) -> {
            for(int i = turnListStub.size() - 1; i >= 0; i--){
                if(turnListStub.get(i).getName().equals(gameManagerStub.getCurrentPlayer().getName())) {
                    turnListStub.remove(i);
                    break;
                }
            }
            turnListStub.add(1, gameManagerStub.getCurrentPlayer());
            return true;
        }).when(gameManagerStub).samePlayerAgain();

        try {
            doAnswer((invocation) -> {
                int row = invocation.getArgument(1);
                int col = invocation.getArgument(2);
                Die toPlace = invocation.getArgument(0);
                ArrayList<Die> adjacentDice = getAdjacents(row, col);

                if(adjacentDice.isEmpty()) {
                    placedDiceStub[row][col] = toPlace;
                    draftPoolStub.remove(toPlace);
                }
                else
                    throw new Exception("There are dice around here");
                return true;
            }).when(playerStub).placeDie(any(Die.class), any(int.class), any(int.class), eq(false));
        } catch (PositionOccupiedException |PatternConstraintViolatedException | RuleViolatedException e) {
            e.printStackTrace();
        }

        // When playerStub.moveDice is called, move the dice in placedDiceStub
        try {
            doAnswer((invocation) -> {
                LinkedList<Die> queue = new LinkedList<>();
                for(Coordinate position : (List<Coordinate>)invocation.getArgument(0)){
                    queue.add(placedDiceStub[position.getRow()][position.getCol()]);
                    placedDiceStub[position.getRow()][position.getCol()] = null;
                }
                for(Coordinate finalPos : (List<Coordinate>)invocation.getArgument(1)){
                    placedDiceStub[finalPos.getRow()][finalPos.getCol()] = queue.remove(0);
                }
                return true;
            }).when(playerStub).moveDice(any(List.class), any(List.class), any(boolean.class), any(boolean.class), any(boolean.class));
        } catch (RuleViolatedException | PatternConstraintViolatedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void pinzaSgrossatriceTest(){
        PinzaSgrossatrice tested = new PinzaSgrossatrice();
        boolean exceptionThrown = false;

        //Asserting that a die with a value different from 6 can be increased
        int randomColourIndex = rndGen.nextInt(5);
        int randomValue = rndGen.nextInt(5) + 1;
        Die addedDie = new Die(randomValue, Colour.validColours().get(randomColourIndex));
        draftPoolStub.add(addedDie);
        tested.setChoosenDie(addedDie);
        tested.setToBeIncreased(true);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Assert draftPool size didn't change
        Assert.assertEquals(6, draftPoolStub.size());
        //Getting the die modified from the draftpool
        for(Die die : draftPoolStub){
            if(!die.getColour().equals(Colour.WHITE))
                addedDie = die;
        }
        // Assert that the die didn't change its colour and that it incremented its value
        Assert.assertEquals(Colour.validColours().get(randomColourIndex), addedDie.getColour());
        Assert.assertEquals(randomValue + 1, addedDie.getValue());

        draftPoolStub.remove(addedDie);

        // Asserting that a die with a value different from 1 can be decreased
        randomColourIndex = rndGen.nextInt(5);
        randomValue = rndGen.nextInt(5) + 2;
        addedDie = new Die(randomValue, Colour.validColours().get(randomColourIndex));
        draftPoolStub.add(addedDie);
        tested.setChoosenDie(addedDie);
        tested.setToBeIncreased(false);
        try{
            tested.applyEffect(playerStub, gameManagerStub);
        }catch(Exception e){
            e.printStackTrace();
        }
        Assert.assertEquals(6, draftPoolStub.size());
        for(Die die : draftPoolStub){
            if(!die.getColour().equals(Colour.WHITE))
                addedDie = die;
        }
        Assert.assertEquals(Colour.validColours().get(randomColourIndex), addedDie.getColour());
        Assert.assertEquals(randomValue - 1, addedDie.getValue());

        // Testing exception throwing
        draftPoolStub.remove(addedDie);

        // Asserting that a die with 1 as value can't be decreased
        randomColourIndex = rndGen.nextInt(5);
        addedDie = new Die(1, Colour.validColours().get(randomColourIndex));
        draftPoolStub.add(addedDie);
        tested.setChoosenDie(addedDie);
        tested.setToBeIncreased(false);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
        exceptionThrown = false;

        draftPoolStub.remove(addedDie);

        // Asserting that a die with 6 as value can't be increased
        randomColourIndex = rndGen.nextInt(5);
        addedDie = new Die(6, Colour.validColours().get(randomColourIndex));
        draftPoolStub.add(addedDie);
        tested.setChoosenDie(addedDie);
        tested.setToBeIncreased(true);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
    }

    @Test
    public void pennelloEglomiseTest(){
        PennelloPerEglomise tested = new PennelloPerEglomise();

        Coordinate randomStartCoord = getRandomCoord();
        Coordinate randomEndCoord = getRandomCoord();
        int randomValue = rndGen.nextInt(6) + 1;
        int randomColourIndex = rndGen.nextInt(5);
        Die movedDie = new Die(randomValue, Colour.validColours().get(randomColourIndex));
        placedDiceStub[randomStartCoord.getRow()][randomStartCoord.getCol()] = movedDie;
        tested.setStartPosition(randomStartCoord);
        tested.setEndPosition(randomEndCoord);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!randomStartCoord.equals(randomEndCoord))
            Assert.assertEquals(null, placedDiceStub[randomStartCoord.getRow()][randomStartCoord.getCol()]);
        Assert.assertEquals(movedDie, placedDiceStub[randomEndCoord.getRow()][randomEndCoord.getCol()]);
    }

    @Test
    public void alesatoreLaminaRameTest(){
        AlesatoreLaminaRame tested = new AlesatoreLaminaRame();

        Coordinate randomStartCoord = getRandomCoord();
        Coordinate randomEndCoord = getRandomCoord();
        int randomValue = rndGen.nextInt(6) + 1;
        int randomColourIndex = rndGen.nextInt(5);
        Die movedDie = new Die(randomValue, Colour.validColours().get(randomColourIndex));
        placedDiceStub[randomStartCoord.getRow()][randomStartCoord.getCol()] = movedDie;
        tested.setStartPosition(randomStartCoord);
        tested.setEndPosition(randomEndCoord);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!randomStartCoord.equals(randomEndCoord))
            Assert.assertEquals(null, placedDiceStub[randomStartCoord.getRow()][randomStartCoord.getCol()]);
        Assert.assertEquals(movedDie, placedDiceStub[randomEndCoord.getRow()][randomEndCoord.getCol()]);
    }

    @Test
    public void lathekinTest(){
        Lathekin tested = new Lathekin();
        LinkedList<Coordinate> randomStartCoord = new LinkedList<>();
        LinkedList<Coordinate> randomEndCoord = new LinkedList<>();
        LinkedList<Die> movedDice = new LinkedList<>();
        Coordinate randomCoord;

        randomStartCoord.add(getRandomCoord());
        do{
            randomCoord = getRandomCoord();
        }while(randomStartCoord.contains(randomCoord));
        randomStartCoord.add(randomCoord);
        randomEndCoord.add(getRandomCoord());
        do {
            randomCoord = getRandomCoord();
        }while(randomEndCoord.contains(randomCoord));
        randomEndCoord.add(randomCoord);
        movedDice.add(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5))));
        movedDice.add(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5))));
        placedDiceStub[randomStartCoord.get(0).getRow()][randomStartCoord.get(0).getCol()] = movedDice.get(0);
        placedDiceStub[randomStartCoord.get(1).getRow()][randomStartCoord.get(1).getCol()] = movedDice.get(1);
        tested.setFirstDieStartPosition(randomStartCoord.get(0));
        tested.setFirstDieEndPosition(randomEndCoord.get(0));
        tested.setSecondDieStartPosition(randomStartCoord.get(1));
        tested.setSecondDieEndPosition(randomEndCoord.get(1));

        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < movedDice.size(); i++){
            if(!randomEndCoord.contains(randomStartCoord.get(i))){
                Assert.assertNull(placedDiceStub[randomStartCoord.get(i).getRow()][randomStartCoord.get(i).getCol()]);
            }
            Assert.assertEquals(movedDice.get(i), placedDiceStub[randomEndCoord.get(i).getRow()][randomEndCoord.get(i).getCol()]);
        }
    }

    @Test
    public void taglierinaCircolareTest(){
        TaglierinaCircolare tested = new TaglierinaCircolare();

        int randomColourIndex = rndGen.nextInt(5);
        int randomValue = rndGen.nextInt(6) + 1;
        Die fromRoundTracker = new Die(randomValue, Colour.validColours().get(randomColourIndex));
        diceLeftStub.add(fromRoundTracker);
        randomColourIndex = rndGen.nextInt(5);
        randomValue = rndGen.nextInt(6) + 1;
        Die fromDraftPool = new Die(randomValue, Colour.validColours().get(randomColourIndex));
        draftPoolStub.add(fromDraftPool);
        tested.setDieFromDraft(fromDraftPool);
        tested.setDieFromRoundTracker(fromRoundTracker);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(diceLeftStub.contains(fromDraftPool));
        Assert.assertTrue(draftPoolStub.contains(fromRoundTracker));
    }

    // TODO: test for pennello per pasta salda

    @Test
    public void martellettoTest(){
        Martelletto tested = new Martelletto();

        ArrayList<Die> oldDraftPool = new ArrayList<>(draftPoolStub);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0; i < draftPoolStub.size(); i++){
            Assert.assertNotSame(oldDraftPool.get(i), draftPoolStub.get(i));
        }
    }

    @Test
    public void tenagliaRotelleTest(){
        TenagliaRotelle tested = new TenagliaRotelle();
        boolean exceptionThrown = false;
        String message;

        Player fstPlayer = mock(Player.class);
        when(playerStub.getName()).thenReturn("Pippo");
        when(fstPlayer.getName()).thenReturn("Gianfranco");
        turnListStub.add(0, fstPlayer);
        int fullTurnListSize = turnListStub.size() + 1;

        for(int i = 0; i < fullTurnListSize / 2; i++) {
            turnListStub.add(turnListStub.size() - (i), fstPlayer);
            try {
                tested.applyEffect(playerStub, gameManagerStub);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Assert.assertSame(fstPlayer, turnListStub.get(0));
            Assert.assertSame(fstPlayer, turnListStub.get(1));
            Assert.assertEquals(2, turnListStub.stream().filter(player -> player.getName().equals(turnListStub.get(0).getName())).count());
            Assert.assertEquals(fullTurnListSize, turnListStub.size());
            turnListStub.remove(1);
        }
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            message = e.getMessage();
            exceptionThrown = true;
            Assert.assertEquals("You can't play this toolcard: you can only use this during your first turn in the round", message);
        }
        Assert.assertTrue(exceptionThrown);

    }

    @Test
    public void rigaSugheroTest(){
        // TODO: check method. Exception die passed not in the list at line 464
        RigaSughero tested = new RigaSughero();
        boolean exceptionThrown = false;
        String message = "There are dice around here";
        ArrayList<Coordinate> posToTest;
        ArrayList<Die> diceToTest;

        Die dieToPlace = new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5)));
        Coordinate randomPosition = getRandomCoord();
        draftPoolStub.add(dieToPlace);
        tested.setChosenDie(dieToPlace);
        tested.setPosition(randomPosition);
        for(Die die : getAdjacents(randomPosition.getRow(), randomPosition.getCol()))
            System.out.println(die);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals(dieToPlace, placedDiceStub[randomPosition.getRow()][randomPosition.getCol()]);

        posToTest = getAdjacentPositions(randomPosition.getRow(), randomPosition.getCol());
        diceToTest = getRandomDice(posToTest.size());
        for(int i = 0; i < posToTest.size(); i++){
            draftPoolStub.add(diceToTest.get(i));
            tested.setChosenDie(diceToTest.get(i));
            tested.setPosition(posToTest.get(i));
            try {
                tested.applyEffect(playerStub, gameManagerStub);
            } catch (Exception e) {
                exceptionThrown = true;
                Assert.assertEquals(message, e.getMessage());
            }
            Assert.assertTrue(exceptionThrown);
            exceptionThrown = false;
        }
        placedDiceStub[randomPosition.getRow()][randomPosition.getCol()] = null;
        draftPoolStub.add(dieToPlace);

        for(int i = 0; i < posToTest.size(); i++){
            draftPoolStub.add(diceToTest.get(i));
            tested.setChosenDie(diceToTest.get(i));
            tested.setPosition(posToTest.get(i));
            try {
                tested.applyEffect(playerStub, gameManagerStub);
            } catch (Exception e) {
                exceptionThrown = true;
                Assert.assertEquals(message, e.getMessage());
            }
            Assert.assertEquals(diceToTest.get(i), placedDiceStub[posToTest.get(i).getRow()][posToTest.get(i).getCol()]);
            Assert.assertFalse(exceptionThrown);
        }

        tested.setChosenDie(dieToPlace);
        tested.setPosition(randomPosition);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            exceptionThrown = true;
            Assert.assertEquals(message, e.getMessage());
        }
        Assert.assertTrue(exceptionThrown);
    }

    @Test
    public void tamponeDiamantatoTest(){
        TamponeDiamantato tested = new TamponeDiamantato();

        Die toFlip = new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5)));
        Die flipped = toFlip.flipDie();
        draftPoolStub.add(toFlip);
        tested.setChosenDie(toFlip);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(draftPoolStub.contains(flipped));
        Assert.assertFalse(draftPoolStub.contains(toFlip));
        Assert.assertTrue(flipped.getValue() == 7 - toFlip.getValue());
        Assert.assertTrue(flipped.getColour().equals(toFlip.getColour()));
    }

    @Test
    public void taglierinaManualeTest(){
        TaglierinaManuale tested = new TaglierinaManuale();

        Die fromRoundTracker = new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5)));
        ArrayList<Coordinate> startPos = new ArrayList<>();
        ArrayList<Coordinate> endPos = new ArrayList<>();
        ArrayList<Die> diceToMove = new ArrayList<>();
        Coordinate randomCoord;

        for(int i = 0; i < 2; i++){
            diceToMove.add(new Die(rndGen.nextInt(6) + 1, fromRoundTracker.getColour()));
        }
        startPos.add(getRandomCoord());
        do{
            randomCoord = getRandomCoord();
        }while(startPos.contains(randomCoord));
        startPos.add(randomCoord);

        endPos.add(getRandomCoord());
        do{
            randomCoord = getRandomCoord();
        }while(endPos.contains(randomCoord));
        endPos.add(randomCoord);

        diceLeftStub.add(fromRoundTracker);
        for(int i = 0; i < diceToMove.size(); i++){
            placedDiceStub[startPos.get(i).getRow()][startPos.get(i).getCol()] = diceToMove.get(i);
        }
        tested.setDieFromRoundTracker(fromRoundTracker);
        tested.setDiceChosen(startPos);
        tested.setMoveTo(endPos);
        try {
            tested.applyEffect(playerStub, gameManagerStub);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0; i < diceToMove.size(); i++){
            Assert.assertEquals(diceToMove.get(i), placedDiceStub[endPos.get(i).getRow()][endPos.get(i).getCol()]);
            if(!endPos.contains(startPos.get(i))){
                Assert.assertNull(placedDiceStub[startPos.get(i).getRow()][startPos.get(i).getCol()]);
            }
        }
        Assert.assertTrue(diceLeftStub.contains(fromRoundTracker));

    }

    private ArrayList<Die> getAdjacents(int row, int col){
        ArrayList<Die> ret = new ArrayList<>();

        if(row + 1 < placedDiceStub.length)
            ret.add(placedDiceStub[row + 1][col]);
        if(row - 1 >= 0)
            ret.add(placedDiceStub[row - 1][col]);
        if(col + 1 < placedDiceStub[0].length)
            ret.add(placedDiceStub[row][col + 1]);
        if(col - 1 >= 0)
            ret.add(placedDiceStub[row][col - 1]);
        return ret.stream().filter(die -> die != null).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<Die> getRandomDice(int diceNumber){
        ArrayList<Die> ret = new ArrayList<>();
        for(int i = 0; i < diceNumber; i++){
            ret.add(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5))));
        }
        return ret;
    }

    private ArrayList<Coordinate> getAdjacentPositions(int row, int col){
        ArrayList<Coordinate> ret = new ArrayList<>();

        if(row + 1 < placedDiceStub.length)
            ret.add(new Coordinate(row + 1, col));
        if(row - 1 >= 0)
            ret.add(new Coordinate(row - 1, col));
        if(col + 1 < placedDiceStub[0].length)
            ret.add(new Coordinate(row, col + 1));
        if(col - 1 >= 0)
            ret.add(new Coordinate(row, col - 1));
        return ret;
    }

    private Coordinate getRandomCoord(){
        int x, y;

        x = rndGen.nextInt(playerPatternStub.getHeight());
        y = rndGen.nextInt(playerPatternStub.getWidth());
        return new Coordinate(x,y);
    }
}
