package projectIngSoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class App 
{
    public static void main( String[] args ) throws FileNotFoundException, Colour.ColorNotFoundException {

        File file = new File("C:\\Users\\danie\\OneDrive - Politecnico di Milano\\IngInf_Mio\\Progetto - SWengi\\ing-soft-2018-paltenghi-parravicini-pellizzi\\src\\main\\test.txt");

        WindowPattern window = new WindowPattern(new Scanner(file));

        System.out.println(window);

        Die myDie = new Die(0, Colour.RED);
        System.out.println(myDie);

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
