package projectIngSoft;


import org.junit.Test;

import java.util.ArrayList;

public class DiceTest {
    @Test
    public void simpleCreationTest() {
        Die myDie = new Die(0, Colour.RED);
        System.out.println(myDie);
    }

    @Test
    public void Rolling90Test(){
        ArrayList<Die> diceBag = new ArrayList<Die>();
        for (Colour c : Colour.validColours()){
            Die newDie = new Die(c);
            for(int i = 1; i <= 18; i++){

                diceBag.add(newDie.rollDie());
            }
        }
        System.out.println(diceBag);
    }

}
