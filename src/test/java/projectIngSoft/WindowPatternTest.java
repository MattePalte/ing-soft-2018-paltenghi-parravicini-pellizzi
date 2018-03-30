package projectIngSoft;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class WindowPatternTest {
    @Test
    public void CreationTest() throws Colour.ColorNotFoundException, FileNotFoundException {
        File file = new File("src/main/test.txt");
        WindowPattern window = new WindowPattern(new Scanner(file));

        System.out.println(window);
    }

}
