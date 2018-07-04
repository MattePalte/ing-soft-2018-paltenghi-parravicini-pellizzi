package project.ing.soft;


import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.WindowPatternCard;
import org.junit.*;

import java.io.File;
import java.util.Scanner;


public class WindowPatternTest {
    @Test
    public void CreationTest() throws Exception {
        File file = new File("src/main/resources/patterns.txt");
        Scanner input = new Scanner(file);
        int nrOfCouple = input.nextInt();
        input.nextLine();
        for (int i = 0; i < nrOfCouple; i++) {
            WindowPatternCard window = WindowPatternCard.loadAPatternCardFromScanner(input);
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
