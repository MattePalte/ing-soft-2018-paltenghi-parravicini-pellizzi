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
        Die myDie = new Die(0, Colour.RED);
        ArrayList<Die> diceBag = new ArrayList<Die>();
        for (Colour c : Colour.values()){
            for(int i = 1; i <= 6; i++){
                Die newDie = new Die(i, c);
                diceBag.add(newDie);
            }
        }

        System.out.println(myDie);

        System.out.println(diceBag);
    }
}
