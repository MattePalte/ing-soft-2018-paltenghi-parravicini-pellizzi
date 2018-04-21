package projectIngSoft;

import javafx.util.Pair;
import org.junit.*;
import projectIngSoft.Cards.Objectives.ObjectiveCard;
import projectIngSoft.Cards.Objectives.Privates.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.View.LocalViewCli;
import projectIngSoft.exceptions.GameInvalidException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class ObjectivesTest {

    private Player player;
    private ObjectiveCard tested;
    private Random rndGen = new Random();

    @Before
    public void createPlayer() {
        player = new Player("TestName", new LocalViewCli("TestName"));
        WindowPatternCard pattern = null;
        try {
            pattern = WindowPatternCard.loadFromScanner(new Scanner(new File("src/main/patterns.txt")));
        } catch (Colour.ColorNotFoundException e) {
            e.printStackTrace();
        } catch (GameInvalidException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        player.setPatternCard(pattern);
    }


    // Testing public objectives' functionality
    @Test
    public void testColoriDiversiColonna() {
        tested = new ColoriDiversiColonna();

        for(int i = 0; i < player.getPattern().getHeight(); i++){
            player.placeDieWithoutConstraints(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i)),i,0);
            player.placeDieWithoutConstraints(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i)),i,2);

        }
        Assert.assertEquals(2,tested.checkCondition(player));

        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 3, 2);

        Assert.assertEquals(1, tested.checkCondition(player));
    }

    @Test
    public void testColoriDiversiRiga(){
        tested = new ColoriDiversiRiga();

        for(int i = 0; i < player.getPattern().getWidth(); i++){
            player.placeDieWithoutConstraints(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i)), 0, i);
            player.placeDieWithoutConstraints(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(i)), 3, i);
        }

        Assert.assertEquals(2, tested.checkCondition(player));

        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 3, 2);

        Assert.assertEquals(1, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureChiare(){
        tested = new SfumatureChiare();
        Pair<Integer, Integer> position;

        for(int i = 0; i < 2; i++){
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(1, Colour.validColours().get(rndGen.nextInt(5))), position.getKey(), position.getValue());
        }
        for(int i = 0; i < 3; i++){
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(2, Colour.validColours().get(rndGen.nextInt(5))), position.getKey(), position.getValue());
        }

        Assert.assertEquals(2, tested.checkCondition(player));

        position = randomIndexes(player.getPlacedDice());
        player.placeDieWithoutConstraints(new Die( 1, Colour.WHITE), position.getKey(), position.getValue());

        Assert.assertEquals(3, tested.checkCondition(player));

    }

    @Test
    public void testSfumatureMedie(){
        tested = new SfumatureMedie();
        Pair<Integer, Integer> position;

        for(int i = 0; i < 2; i++){
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(3, Colour.validColours().get(rndGen.nextInt(5))), position.getKey(), position.getValue());
        }
        for(int i = 0; i < 3; i++){
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(4, Colour.validColours().get(rndGen.nextInt(5))), position.getKey(), position.getValue());
        }

        Assert.assertEquals(2, tested.checkCondition(player));

        position = randomIndexes(player.getPlacedDice());
        player.placeDieWithoutConstraints(new Die( 3, Colour.WHITE), position.getKey(), position.getValue());

        Assert.assertEquals(3, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureScure(){
        tested = new SfumatureScure();
        Pair<Integer, Integer> position;

        for(int i = 0; i < 2; i++){
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(5, Colour.validColours().get(rndGen.nextInt(5))), position.getKey(), position.getValue());
        }
        for(int i = 0; i < 3; i++){
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(6, Colour.validColours().get(rndGen.nextInt(5))), position.getKey(), position.getValue());
        }

        Assert.assertEquals(2, tested.checkCondition(player));

        position = randomIndexes(player.getPlacedDice());
        player.placeDieWithoutConstraints(new Die( 5, Colour.WHITE), position.getKey(), position.getValue());

        Assert.assertEquals(3, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureDiverse(){
        tested = new SfumatureDiverse();
        Pair<Integer, Integer> position;

        player.placeDieWithoutConstraints(new Die(6, Colour.WHITE), 0, 0);
        for(int i = 0; i < 5; i++){
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(i + 1, Colour.validColours().get(rndGen.nextInt(5))), position.getKey(), position.getValue());
        }

        Assert.assertEquals(1, tested.checkCondition(player));

        player.placeDieWithoutConstraints(new Die(1, Colour.WHITE), 0, 0);

        Assert.assertEquals(0, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureDiverseRiga() {
        tested = new SfumatureDiverseRiga();

        for(int col = 0; col < player.getPattern().getWidth(); col++){
            player.placeDieWithoutConstraints(new Die(col + 1, Colour.validColours().get(rndGen.nextInt(5))), 0, col);
            player.placeDieWithoutConstraints(new Die(col + 1, Colour.validColours().get(rndGen.nextInt(5))), 3, col);

        }
        Assert.assertEquals(2, tested.checkCondition(player));

        player.placeDieWithoutConstraints(new Die(1, Colour.WHITE), 3, 4);

        Assert.assertEquals(1, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureDiverseColonna(){
        tested = new SfumatureDiverseColonna();

        for(int row = 0; row < player.getPattern().getHeight(); row++){
            player.placeDieWithoutConstraints(new Die(row + 1, Colour.validColours().get(rndGen.nextInt(5))), row, 0);
            player.placeDieWithoutConstraints(new Die(row + 1, Colour.validColours().get(rndGen.nextInt(5))), row, 2);
        }
        Assert.assertEquals(2, tested.checkCondition(player));

        player.placeDieWithoutConstraints(new Die(1, Colour.WHITE), 2, 2);

        Assert.assertEquals(1, tested.checkCondition(player));
    }

    @Test
    public void testVarietaColore(){
        tested = new VarietaColore();
        Pair<Integer, Integer> position;

        player.placeDieWithoutConstraints(new Die(1, Colour.VIOLET), 0, 0);

        Assert.assertEquals(0, tested.checkCondition(player));

        for (Colour colour : Colour.validColours()) {
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(rndGen.nextInt(6) + 1, colour), position.getKey(), position.getValue());
        }

        Assert.assertEquals(1, tested.checkCondition(player));

        for (Colour colour : Colour.validColours()) {
            position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(rndGen.nextInt(6) + 1, colour), position.getKey(), position.getValue());
        }

        Assert.assertEquals(2, tested.checkCondition(player));
    }

    @Test
    public void testDiagonaliColorate(){
        tested = new DiagonaliColorate();

        // First test
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 0, 0);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 0, 1);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 1, 0);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 1, 1);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 1, 3);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 2, 2);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 3, 1);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 3, 3);
        player.placeDieWithoutConstraints(new Die(Colour.WHITE), 3, 0);

        Assert.assertEquals(8, tested.checkCondition(player));

        // Second test
        player.placeDieWithoutConstraints(new Die(Colour.RED), 1, 1);

        Assert.assertEquals(6, tested.checkCondition(player));

        //Third test
        player.placeDieWithoutConstraints(new Die(Colour.VIOLET), 0, 1);

        Assert.assertEquals(4, tested.checkCondition(player));

    }


    // Testing private objectives functionality

    @Test
    public void testSfumatureBlu(){
        tested = new SfumatureBlu();
        int height = player.getPattern().getHeight();
        int width = player.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(player, Colour.BLUE);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (player.getPlacedDice()[row][col] == null)
                    player.placeDieWithoutConstraints(new Die(Colour.WHITE), row, col);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(player));

        // Substituting a die not blue with a blue die with value equal to 6
        do{
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while(player.getPlacedDice()[row][col].getColour().equals(Colour.BLUE));
        player.placeDieWithoutConstraints(new Die(6, Colour.BLUE), row, col);

        Assert.assertEquals(27, tested.checkCondition(player));

    }

    @Test
    public void testSfumatureGialle() {
        tested = new SfumatureGialle();
        int height = player.getPattern().getHeight();
        int width = player.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(player, Colour.YELLOW);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (player.getPlacedDice()[row][col] == null)
                    player.placeDieWithoutConstraints(new Die(Colour.WHITE), row, col);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(player));

        // Substituting a die not YELLOW with a YELLOW die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (player.getPlacedDice()[row][col].getColour().equals(Colour.YELLOW));
        player.placeDieWithoutConstraints(new Die(6, Colour.YELLOW), row, col);

        Assert.assertEquals(27, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureRosse() {
        tested = new SfumatureRosse();
        int height = player.getPattern().getHeight();
        int width = player.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(player, Colour.RED);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (player.getPlacedDice()[row][col] == null)
                    player.placeDieWithoutConstraints(new Die(Colour.WHITE), row, col);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(player));

        // Substituting a die not RED with a RED die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (player.getPlacedDice()[row][col].getColour().equals(Colour.RED));
        player.placeDieWithoutConstraints(new Die(6, Colour.RED), row, col);

        Assert.assertEquals(27, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureVerdi() {
        tested = new SfumatureVerdi();
        int height = player.getPattern().getHeight();
        int width = player.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(player, Colour.GREEN);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (player.getPlacedDice()[row][col] == null)
                    player.placeDieWithoutConstraints(new Die(Colour.WHITE), row, col);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(player));

        // Substituting a die not GREEN with a GREEN die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (player.getPlacedDice()[row][col].getColour().equals(Colour.GREEN));
        player.placeDieWithoutConstraints(new Die(6, Colour.GREEN), row, col);

        Assert.assertEquals(27, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureViola() {
        tested = new SfumatureViola();
        int height = player.getPattern().getHeight();
        int width = player.getPattern().getWidth();
        int row = 0;
        int col = 0;

        setPrivateObjTest(player, Colour.VIOLET);

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                if (player.getPlacedDice()[row][col] == null)
                    player.placeDieWithoutConstraints(new Die(Colour.WHITE), row, col);
            }
        }

        Assert.assertEquals(21, tested.checkCondition(player));

        // Substituting a die not VIOLET with a VIOLET die with value equal to 6
        do {
            row = rndGen.nextInt(4);
            col = rndGen.nextInt(5);
        }
        while (player.getPlacedDice()[row][col].getColour().equals(Colour.VIOLET));
        player.placeDieWithoutConstraints(new Die(6, Colour.VIOLET), row, col);

        Assert.assertEquals(27, tested.checkCondition(player));
    }

    // Needed in private objectives test to place a set of 6 dice of the chosen colour in the pattern of the player
    private void setPrivateObjTest(Player player, Colour colour){
        int row = 0;
        int col = 0;

        for(int i = 0; i < 6; i++){
            Pair<Integer, Integer> position = randomIndexes(player.getPlacedDice());
            player.placeDieWithoutConstraints(new Die(i + 1, colour), position.getKey(), position.getValue());
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
