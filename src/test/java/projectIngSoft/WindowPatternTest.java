package projectIngSoft;


import org.junit.*;

import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.exceptions.GameInvalidException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class WindowPatternTest {
    @Test
    public void CreationTest() throws Exception {
        File file = new File("src/main/patterns.txt");
        Scanner input = new Scanner(file);


        for(int i = 0; i < 12; i++) {
            WindowPatternCard window = null;
            try {
                window = WindowPatternCard.loadFromScanner(input);
            } catch (GameInvalidException e) {
                e.printStackTrace();
            }
            System.out.println(window.getFrontPattern());
            System.out.println(window.getRearPattern() );
            input.nextLine();

        }
    }

    @Test
    public void  constraintStringSameWidthDice(){
        Constraint c1 = new Constraint(0, Colour.WHITE);
        System.out.println("|"+c1+c1+c1+c1+c1+"|");
        for (int i = 1; i < 6; i++) {
            Die d1= new Die(i, Colour.RED);
            System.out.println("|"+d1+c1+c1+c1+c1+"|");
        }
    }

}
