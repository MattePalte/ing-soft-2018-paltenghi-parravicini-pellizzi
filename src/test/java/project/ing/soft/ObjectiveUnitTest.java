package project.ing.soft;

import project.ing.soft.cards.objectives.ObjectiveCard;
import project.ing.soft.cards.objectives.privates.*;
import project.ing.soft.cards.objectives.publics.*;
import project.ing.soft.cards.WindowPattern;
import javafx.util.Pair;
import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectiveUnitTest {

    private Player playerStub;
    private ObjectiveCard tested;
    private Random rndGen = new Random();
    private Die[][] placedDice;
    private WindowPattern pattern;

    @Before
    public void createPlayer() throws FileNotFoundException, Colour.ColorNotFoundException {
        playerStub = mock(Player.class);
        placedDice = new Die[4][5];
        pattern = WindowPattern.loadFromScanner(new Scanner(new File("src/main/empty_pattern.txt")));
        when(playerStub.getPlacedDice()).thenReturn(placedDice);
        when(playerStub.getPattern()).thenReturn(pattern);
        when(playerStub.getPlacedDice()).thenReturn(placedDice);
    }


    // Testing public objectives' functionality
    @Test
    public void testColoriDiversiColonna() {
        tested = new ColoriDiversiColonna();

        for(int i = 0; i < playerStub.getPattern().getHeight(); i++){
            placedDice[i][0]= new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i));
            placedDice[i][2]= new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i));

        }
        Assert.assertEquals(2,tested.checkCondition(playerStub));

        placedDice[3][2] = (new Die(Colour.validColours().get(0)));

        Assert.assertEquals(1, tested.checkCondition(playerStub));
    }

    @Test
    public void testColoriDiversiRiga(){
        tested = new ColoriDiversiRiga();

        for(int i = 0; i < playerStub.getPattern().getWidth(); i++){
            placedDice[0][i] = new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i));
            placedDice[3][i] = new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i));
        }

        Assert.assertEquals(2, tested.checkCondition(playerStub));

        placedDice[3][2] = new Die(Colour.validColours().get(0));

        Assert.assertEquals(1, tested.checkCondition(playerStub));
    }

    @Test
    public void testSfumatureChiare(){
        tested = new SfumatureChiare();
        Pair<Integer, Integer> position;

        for(int i = 0; i < 2; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(1, Colour.validColours().get(rndGen.nextInt(5)));
        }
        for(int i = 0; i < 3; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(2, Colour.validColours().get(rndGen.nextInt(5)));
        }

        Assert.assertEquals(2, tested.checkCondition(playerStub));

        position = randomIndexes(playerStub.getPlacedDice());
        placedDice[position.getKey()][position.getValue()] = new Die( 1, Colour.WHITE);

        Assert.assertEquals(3, tested.checkCondition(playerStub));

        for(int i = 0; i < 3; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(3, Colour.WHITE);
        }

        Assert.assertEquals(3, tested.checkCondition(playerStub));

    }

    @Test
    public void testSfumatureMedie(){
        tested = new SfumatureMedie();
        Pair<Integer, Integer> position;

        for(int i = 0; i < 2; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(3, Colour.validColours().get(rndGen.nextInt(5)));
        }
        for(int i = 0; i < 3; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(4, Colour.validColours().get(rndGen.nextInt(5)));
        }

        Assert.assertEquals(2, tested.checkCondition(playerStub));

        position = randomIndexes(playerStub.getPlacedDice());
        placedDice[position.getKey()][position.getValue()] = new Die( 3, Colour.WHITE);

        Assert.assertEquals(3, tested.checkCondition(playerStub));

        for(int i = 0; i < 3; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(1, Colour.WHITE);
        }

        Assert.assertEquals(3, tested.checkCondition(playerStub));

    }

    @Test
    public void testSfumatureScure(){
        tested = new SfumatureScure();
        Pair<Integer, Integer> position;

        for(int i = 0; i < 2; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(5, Colour.validColours().get(rndGen.nextInt(5)));
        }
        for(int i = 0; i < 3; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(6, Colour.validColours().get(rndGen.nextInt(5)));
        }

        Assert.assertEquals(2, tested.checkCondition(playerStub));

        position = randomIndexes(playerStub.getPlacedDice());
        placedDice[position.getKey()][position.getValue()] = new Die( 5, Colour.WHITE);

        Assert.assertEquals(3, tested.checkCondition(playerStub));

        for(int i = 0; i < 3; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(4, Colour.WHITE);
        }

        Assert.assertEquals(3, tested.checkCondition(playerStub));

    }

    @Test
    public void testSfumatureDiverse(){
        tested = new SfumatureDiverse();
        Pair<Integer, Integer> position;

        placedDice[0][0] = new Die(6, Colour.WHITE);
        for(int i = 0; i < 5; i++){
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(i + 1, Colour.validColours().get(rndGen.nextInt(5)));
        }

        Assert.assertEquals(1, tested.checkCondition(playerStub));

        placedDice[0][0] = new Die(1, Colour.WHITE);

        Assert.assertEquals(0, tested.checkCondition(playerStub));
    }

    @Test
    public void testSfumatureDiverseRiga() {
        tested = new SfumatureDiverseRiga();

        for(int col = 0; col < playerStub.getPattern().getWidth(); col++){
            placedDice[0][col] = new Die(col + 1, Colour.validColours().get(rndGen.nextInt(5)));
            placedDice[3][col] = new Die(col + 1, Colour.validColours().get(rndGen.nextInt(5)));

        }
        Assert.assertEquals(2, tested.checkCondition(playerStub));

        placedDice[3][4] = new Die(1, Colour.WHITE);

        Assert.assertEquals(1, tested.checkCondition(playerStub));
    }

    @Test
    public void testSfumatureDiverseColonna(){
        tested = new SfumatureDiverseColonna();

        for(int row = 0; row < playerStub.getPattern().getHeight(); row++){
            placedDice[row][0] = new Die(row + 1, Colour.validColours().get(rndGen.nextInt(5)));
            placedDice[row][2] = new Die(row + 1, Colour.validColours().get(rndGen.nextInt(5)));
        }
        Assert.assertEquals(2, tested.checkCondition(playerStub));

        placedDice[2][2] = new Die(1, Colour.WHITE);

        Assert.assertEquals(1, tested.checkCondition(playerStub));
    }

    @Test
    public void testVarietaColore(){
        tested = new VarietaColore();
        Pair<Integer, Integer> position;

        placedDice[0][0] = new Die(1, Colour.VIOLET);

        Assert.assertEquals(0, tested.checkCondition(playerStub));

        for (Colour colour : Colour.validColours()) {
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(rndGen.nextInt(6) + 1, colour);
        }

        Assert.assertEquals(1, tested.checkCondition(playerStub));

        for (Colour colour : Colour.validColours()) {
            position = randomIndexes(playerStub.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(rndGen.nextInt(6) + 1, colour);
        }

        Assert.assertEquals(2, tested.checkCondition(playerStub));
    }

    @Test
    public void testDiagonaliColorate(){
        tested = new DiagonaliColorate();

        // First test
        placedDice[0][0] = new Die(Colour.WHITE);
        placedDice[0][1] = new Die(Colour.WHITE);
        placedDice[1][0] = new Die(Colour.WHITE);
        placedDice[1][1] = new Die(Colour.WHITE);
        placedDice[1][3] = new Die(Colour.WHITE);
        placedDice[2][2] = new Die(Colour.WHITE);
        placedDice[3][1] = new Die(Colour.WHITE);
        placedDice[3][3] = new Die(Colour.WHITE);
        placedDice[3][0] = new Die(Colour.WHITE);

        Assert.assertEquals(9, tested.checkCondition(playerStub));

        // Second test
        placedDice[1][1] = new Die(Colour.RED);

        Assert.assertEquals(7, tested.checkCondition(playerStub));

        //Third test
        placedDice[0][1] = new Die(Colour.VIOLET);

        Assert.assertEquals(5, tested.checkCondition(playerStub));

        //Fourth Test
        placedDice[0][4] = new Die(Colour.WHITE);

        Assert.assertEquals(6, tested.checkCondition(playerStub));

    }


    // Testing private objectives functionality

    @Test
    public void testSfumatureBlu(){
        tested = new SfumatureBlu();
        int height = playerStub.getPattern().getHeight();
        int width = playerStub.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(playerStub, Colour.BLUE);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (playerStub.getPlacedDice()[row][col] == null)
                    placedDice[row][col] = new Die(Colour.WHITE);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(playerStub));

        // Substituting a die not blue with a blue die with value equal to 6
        do{
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while(playerStub.getPlacedDice()[row][col].getColour().equals(Colour.BLUE));
        placedDice[row][col] = new Die(6, Colour.BLUE);

        Assert.assertEquals(27, tested.checkCondition(playerStub));

    }

    @Test
    public void testSfumatureGialle() {
        tested = new SfumatureGialle();
        int height = playerStub.getPattern().getHeight();
        int width = playerStub.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(playerStub, Colour.YELLOW);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (playerStub.getPlacedDice()[row][col] == null)
                    placedDice[row][col] = new Die(Colour.WHITE);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(playerStub));

        // Substituting a die not YELLOW with a YELLOW die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (playerStub.getPlacedDice()[row][col].getColour().equals(Colour.YELLOW));
        placedDice[row][col] = new Die(6, Colour.YELLOW);

        Assert.assertEquals(27, tested.checkCondition(playerStub));
    }

    @Test
    public void testSfumatureRosse() {
        tested = new SfumatureRosse();
        int height = playerStub.getPattern().getHeight();
        int width = playerStub.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(playerStub, Colour.RED);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (playerStub.getPlacedDice()[row][col] == null)
                    placedDice[row][col] = new Die(Colour.WHITE);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(playerStub));

        // Substituting a die not RED with a RED die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (playerStub.getPlacedDice()[row][col].getColour().equals(Colour.RED));
        placedDice[row][col] = new Die(6, Colour.RED);

        Assert.assertEquals(27, tested.checkCondition(playerStub));
    }

    @Test
    public void testSfumatureVerdi() {
        tested = new SfumatureVerdi();
        int height = playerStub.getPattern().getHeight();
        int width = playerStub.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(playerStub, Colour.GREEN);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (playerStub.getPlacedDice()[row][col] == null)
                    placedDice[row][col] = new Die(Colour.WHITE);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(playerStub));

        // Substituting a die not GREEN with a GREEN die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (playerStub.getPlacedDice()[row][col].getColour().equals(Colour.GREEN));
        placedDice[row][col] = new Die(6, Colour.GREEN);

        Assert.assertEquals(27, tested.checkCondition(playerStub));
    }

    @Test
    public void testSfumatureViola() {
        tested = new SfumatureViola();
        int height = playerStub.getPattern().getHeight();
        int width = playerStub.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(playerStub, Colour.VIOLET);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (playerStub.getPlacedDice()[row][col] == null)
                    placedDice[row][col] = new Die(Colour.WHITE);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(playerStub));

        // Substituting a die not VIOLET with a VIOLET die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (playerStub.getPlacedDice()[row][col].getColour().equals(Colour.VIOLET));
        placedDice[row][col] = new Die(6, Colour.VIOLET);

        Assert.assertEquals(27, tested.checkCondition(playerStub));
    }

    private Die[][] placeDie(Die aDie, int row, int col){
        placedDice[row][col] = aDie;
        return placedDice;
    }

    // Needed in private objectives test to place a set of 6 dice of the chosen colour in the pattern of the playerStub
    private void setPrivateObjTest(Player player, Colour colour){
        int row = 0;
        int col = 0;

        for(int i = 0; i < 6; i++){
            Pair<Integer, Integer> position = randomIndexes(player.getPlacedDice());
            placedDice[position.getKey()][position.getValue()] = new Die(i + 1, colour);
        }
    }

    // Returns random row and col indexes of a position not occupied
    private Pair<Integer, Integer> randomIndexes(Die[][] placedDice){
        int row = 0;
        int col = 0;

        do{
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while(placedDice[row][col] != null);
        return new Pair<>(row, col);
    }
}
