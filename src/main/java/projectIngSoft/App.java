package projectIngSoft;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class App 
{
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );
        Die myDie = new Die(0, Colour.RED);
        ArrayList<Die> diceBag = new ArrayList<Die>();
        for (Colour c : Colour.validColours()){
            Die newDie = new Die(c);
            for(int i = 1; i <= 18; i++){

                diceBag.add(newDie.rollDie());
            }
        }

        System.out.println(myDie);

        System.out.println(diceBag);
    }
}
