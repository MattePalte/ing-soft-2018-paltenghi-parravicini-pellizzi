package project.ing.soft;


import org.junit.Test;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;

import java.io.IOException;
import java.util.ArrayList;

public class DiceTest {
    @Test
    public void simpleCreationTest() throws IOException {
        Die myDie = new Die(2, Colour.RED);
        System.out.println(myDie.getImgPath());
        System.out.write(myDie.toString().getBytes());
    }

    @Test
    public void Rolling90Test(){
        ArrayList<Die> diceBag = new ArrayList<>();
        for (Colour c : Colour.validColours()){
            Die newDie = new Die(c);
            for(int i = 1; i <= 18; i++){

                diceBag.add(newDie.rollDie());
            }
        }
        System.out.println(diceBag);
    }


}
