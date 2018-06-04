package project.ing.soft;

import org.junit.Assert;
import org.junit.Test;
import project.ing.soft.model.Coordinate;

import java.util.Random;

public class CoordinateTest {

    private Random rndGen = new Random();

    @Test
    public void constructorTest(){
        int row = rndGen.nextInt();
        int col = rndGen.nextInt();
        Coordinate toBeTested = new Coordinate(row, col);
        Assert.assertEquals(row, toBeTested.getRow());
        Assert.assertEquals(col, toBeTested.getCol());
        Assert.assertEquals("(" + row + ", " + col + ")", toBeTested.toString());
    }

    @Test
    public void productorTest(){
        int row = rndGen.nextInt();
        int col = rndGen.nextInt();
        Coordinate toBeCopied = new Coordinate(row, col);
        Coordinate copy = new Coordinate(toBeCopied);
        Assert.assertEquals(toBeCopied, copy);
        Assert.assertFalse(copy == toBeCopied);
    }
}
