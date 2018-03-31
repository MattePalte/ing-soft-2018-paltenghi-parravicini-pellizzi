package projectIngSoft;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WindowPatternTest {
    @Test
    public void CreationTest() throws Colour.ColorNotFoundException, FileNotFoundException {
        File file = new File("src/main/patterns.txt");
        Scanner input = new Scanner(file);
        String cardRepr = new String("");
        Scanner PatternBuilder;

        /*for(int i = 0; i < 4; i++) {
            cardRepr.append(input.nextLine());
            cardRepr.append("\n");
        }*/


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
