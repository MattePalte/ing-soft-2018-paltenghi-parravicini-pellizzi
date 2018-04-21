package projectIngSoft;

import org.junit.*;
import projectIngSoft.Cards.Objectives.Publics.*;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.View.LocalViewCli;
import projectIngSoft.exceptions.GameInvalidException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ObjectivesTest {

    private Player player;
    private PublicObjective tested;

    @Before
    public void createPlayer() {
        player = new Player("TestName", new LocalViewCli());
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


    // Testing objectives' functionality
    @Test
    public void testColoriDiversiColonna() {
        tested = new ColoriDiversiColonna();

        for(int i = 0; i < player.getPattern().getHeight(); i++){
            player.placeDieWithoutConstraints(new Die(Colour.validColours().get(i)),i,0);
            player.placeDieWithoutConstraints(new Die(Colour.validColours().get(i)),i,2);

        }
        Assert.assertEquals(2,tested.checkCondition(player));
    }

    @Test
    public void testColoriDiversiRiga(){
        tested = new ColoriDiversiRiga();

        for(int i = 0; i < player.getPattern().getWidth(); i++){
            player.placeDieWithoutConstraints(new Die(Colour.validColours().get(i)), 0, i);
            player.placeDieWithoutConstraints(new Die(Colour.validColours().get(i)), 3, i);
        }
        Assert.assertEquals(2, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureChiare(){
        tested = new SfumatureChiare();

        player.placeDieWithoutConstraints(new Die(1, Colour.validColours().get(0)), 0, 0);
        player.placeDieWithoutConstraints(new Die(1, Colour.validColours().get(0)), 0, 3);
        player.placeDieWithoutConstraints(new Die(2, Colour.validColours().get(0)), 1, 2);
        player.placeDieWithoutConstraints(new Die(2,Colour.validColours().get(0)), 3, 1);
        player.placeDieWithoutConstraints(new Die(2, Colour.validColours().get(0)), 2, 0);
        Assert.assertEquals(2, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureMedie(){
        tested = new SfumatureMedie();

        player.placeDieWithoutConstraints(new Die(3, Colour.validColours().get(0)), 0, 0);
        player.placeDieWithoutConstraints(new Die(3, Colour.validColours().get(0)), 0, 3);
        player.placeDieWithoutConstraints(new Die(4, Colour.validColours().get(0)), 1, 2);
        player.placeDieWithoutConstraints(new Die(3,Colour.validColours().get(0)), 3, 1);
        player.placeDieWithoutConstraints(new Die(4, Colour.validColours().get(0)), 2, 0);
        Assert.assertEquals(2, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureScure(){
        tested = new SfumatureScure();

        player.placeDieWithoutConstraints(new Die(5, Colour.validColours().get(0)), 0, 0);
        player.placeDieWithoutConstraints(new Die(5, Colour.validColours().get(0)), 0, 3);
        player.placeDieWithoutConstraints(new Die(6, Colour.validColours().get(0)), 1, 2);
        player.placeDieWithoutConstraints(new Die(5,Colour.validColours().get(0)), 3, 1);
        player.placeDieWithoutConstraints(new Die(6, Colour.validColours().get(0)), 2, 0);
        Assert.assertEquals(2, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureDiverse(){
        tested = new SfumatureDiverse();

        player.placeDieWithoutConstraints(new Die(1, Colour.validColours().get(0)), 0, 0);
        player.placeDieWithoutConstraints(new Die(2, Colour.validColours().get(0)), 0, 3);
        player.placeDieWithoutConstraints(new Die(3, Colour.validColours().get(0)), 1, 2);
        player.placeDieWithoutConstraints(new Die(4,Colour.validColours().get(0)), 3, 1);
        player.placeDieWithoutConstraints(new Die(5, Colour.validColours().get(0)), 2, 0);
        player.placeDieWithoutConstraints(new Die(6, Colour.validColours().get(0)), 1, 0);

        Assert.assertEquals(1, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureDiverseRiga() {
        tested = new SfumatureDiverseRiga();

        for(int col = 0; col < player.getPattern().getWidth(); col++){
            player.placeDieWithoutConstraints(new Die(col + 1, Colour.validColours().get(0)), 0, col);
            player.placeDieWithoutConstraints(new Die(col + 1, Colour.validColours().get(0)), 3, col);

        }
        Assert.assertEquals(2, tested.checkCondition(player));
    }

    @Test
    public void testSfumatureDiverseColonna(){
        tested = new SfumatureDiverseColonna();

        for(int row = 0; row < player.getPattern().getHeight(); row++){
            player.placeDieWithoutConstraints(new Die(row + 1, Colour.validColours().get(0)), row, 0);
            player.placeDieWithoutConstraints(new Die(row + 1, Colour.validColours().get(0)), row, 2);
        }
        Assert.assertEquals(2, tested.checkCondition(player));
    }

    @Test
    public void testVarietaColore(){
        tested = new VarietaColore();

        player.placeDieWithoutConstraints(new Die(1, Colour.validColours().get(0)), 0, 0);
        player.placeDieWithoutConstraints(new Die(2, Colour.validColours().get(1)), 0, 3);
        player.placeDieWithoutConstraints(new Die(3, Colour.validColours().get(2)), 1, 2);
        player.placeDieWithoutConstraints(new Die(4,Colour.validColours().get(3)), 3, 1);
        player.placeDieWithoutConstraints(new Die(5, Colour.validColours().get(4)), 2, 0);
        player.placeDieWithoutConstraints(new Die(6, Colour.validColours().get(0)), 1, 0);
        Assert.assertEquals(1, tested.checkCondition(player));
    }

    @Test
    public void testDiagonaliColorate(){
        tested = new DiagonaliColorate();

        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 0, 0);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 0, 1);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 1, 0);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 1, 1);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 1, 3);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 2, 2);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 3, 1);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 3, 3);
        player.placeDieWithoutConstraints(new Die(Colour.validColours().get(0)), 3, 0);
        Assert.assertEquals(8, tested.checkCondition(player));

    }
}
