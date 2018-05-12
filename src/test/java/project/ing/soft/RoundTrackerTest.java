package project.ing.soft;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.RoundTracker;

import java.util.ArrayList;
import java.util.Random;

public class RoundTrackerTest {

    private RoundTracker roundTracker;
    private Random rndGen = new Random();

    @Before
    public void createRoundTracker(){
        roundTracker = new RoundTracker();
    }

    @Test
    public void creationTest(){
        Assert.assertEquals(1, roundTracker.getCurrentRound());
        Assert.assertEquals(0, roundTracker.getDiceLeftFromRound().size());
    }

    @Test
    public void modifyTest(){
        int randomTurns = rndGen.nextInt(10);
        int diceToAdd = rndGen.nextInt(50);

        for(int i = 0; i < randomTurns; i++){
            roundTracker.nextRound();
        }
        ArrayList<Die> toAdd = new ArrayList();
        for(int i = 0; i < diceToAdd; i++){
            //Adding randomic dice to the list toAdd
            toAdd.add(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5))));
        }
        roundTracker.addDiceLeft(toAdd);
        Assert.assertEquals(1 + randomTurns, roundTracker.getCurrentRound());
        Assert.assertEquals(diceToAdd, roundTracker.getDiceLeftFromRound().size());
        Assert.assertTrue(roundTracker.getDiceLeftFromRound().containsAll(toAdd));
        ArrayList<Die> diceLeftFromRoundCopy = roundTracker.getDiceLeftFromRound();
        for(int i = 0; i < diceToAdd; i++){
            diceLeftFromRoundCopy.remove(roundTracker.getDiceLeftFromRound().get(i));
        }
        Assert.assertEquals(0, diceLeftFromRoundCopy.size());
        Assert.assertEquals(diceToAdd, roundTracker.getDiceLeftFromRound().size());

        Die firstDie = roundTracker.getDiceLeftFromRound().get(0);
        roundTracker.swapDie(new Die(Colour.WHITE), firstDie);
        Assert.assertTrue(roundTracker.getDiceLeftFromRound().contains(new Die(Colour.WHITE)));
        Assert.assertEquals(diceToAdd, roundTracker.getDiceLeftFromRound().size());
        Assert.assertEquals(1 + randomTurns, roundTracker.getCurrentRound());
    }

    @Test
    public void producerTest(){
        int randomTurns = rndGen.nextInt(10);
        int diceToAdd = rndGen.nextInt(50);
        for(int i = 0; i < randomTurns; i++){
            roundTracker.nextRound();
        }

        ArrayList<Die> toAdd = new ArrayList<>();
        for(int i = 0; i < diceToAdd; i++){
            toAdd.add(new Die(rndGen.nextInt(6) + 1, Colour.validColours().get(rndGen.nextInt(5))));
        }

        roundTracker.addDiceLeft(toAdd);

        RoundTracker copy = new RoundTracker(roundTracker);

        Assert.assertEquals(roundTracker.getCurrentRound(), copy.getCurrentRound());
        Assert.assertEquals(roundTracker.getDiceLeftFromRound().size(), copy.getDiceLeftFromRound().size());

        // Verifying if copy contains all and only the dice from roundTracker
        Assert.assertTrue(roundTracker.getDiceLeftFromRound().containsAll(copy.getDiceLeftFromRound()));

        ArrayList<Die> diceLeftFromRoundCopy = copy.getDiceLeftFromRound();
        for(int i = 0; i < diceToAdd; i++){
            diceLeftFromRoundCopy.remove(roundTracker.getDiceLeftFromRound().get(i));
        }

        Assert.assertEquals(0, diceLeftFromRoundCopy.size());

        toAdd.clear();
        toAdd.add(new Die(Colour.WHITE));
        copy.addDiceLeft(toAdd);

        Assert.assertTrue(copy.getDiceLeftFromRound().containsAll(roundTracker.getDiceLeftFromRound()));
        Assert.assertFalse(roundTracker.getDiceLeftFromRound().containsAll(copy.getDiceLeftFromRound()));
        Assert.assertTrue(copy.getDiceLeftFromRound().contains(toAdd.get(0)));
        Assert.assertFalse(roundTracker.getDiceLeftFromRound().contains(toAdd.get(0)));
        diceLeftFromRoundCopy = copy.getDiceLeftFromRound();
        diceLeftFromRoundCopy.removeAll(roundTracker.getDiceLeftFromRound());
        Assert.assertEquals(1, diceLeftFromRoundCopy.size());
    }
}
