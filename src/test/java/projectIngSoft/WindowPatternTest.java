package projectIngSoft;


import org.junit.*;

import projectIngSoft.Cards.WindowPatternCard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class WindowPatternTest {
    @Test
    public void CreationTest() throws Colour.ColorNotFoundException, FileNotFoundException {
        File file = new File("src/main/patterns.txt");
        Scanner input = new Scanner(file);




        for(int i = 0; i < 12; i++) {
            WindowPatternCard window = WindowPatternCard.loadFromScanner(input);
            System.out.println(window);
        }
    }

}
