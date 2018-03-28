package projectIngSoft;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );
        Dice myDice = new Dice(1, Colour.RED);
        ArrayList<Dice> diceBag = new ArrayList<Dice>();
        for (Colour c : Colour.values()){
            for(int i = 1; i <= 6; i++){
                Dice newDice = new Dice(i, c);
                diceBag.add(newDice);
            }
        }

        System.out.println(myDice);

        System.out.println(diceBag);
    }
}
