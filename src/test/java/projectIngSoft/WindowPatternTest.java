package projectIngSoft;


import org.junit.*;
import projectIngSoft.Cards.WindowPattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class WindowPatternTest {
    @Test
    public void CreationTest() throws Colour.ColorNotFoundException, FileNotFoundException {
        File file = new File("src/main/patterns.txt");
        Scanner input = new Scanner(file);
        String cardRepr ;
        Scanner PatternBuilder;




        for(int i = 0; i < 12; i++) {
            cardRepr = "";
            for(int line = 0; line < 4; line++)
                cardRepr = cardRepr + input.nextLine() + "\n";

            PatternBuilder = new Scanner(cardRepr);
            WindowPattern window = new WindowPattern(PatternBuilder);
            System.out.println(window);
        }
    }

}
