package projectIngSoft.Cards;

import projectIngSoft.Colour;

import java.util.Scanner;

public class WindowPatternCard extends Card {
    private WindowPattern frontPattern;
    private WindowPattern rearPattern;

    // requires title != null && description != null && frontPattern != null && rearPattern != null &&
    //          rearPattern != frontPattern && rearPattern.toString() != frontPattern.toString()
    private WindowPatternCard(String title, String description, WindowPattern frontPattern, WindowPattern rearPattern) {
        super(title, description);
        this.frontPattern = frontPattern;
        this.rearPattern = rearPattern;
    }

    public WindowPattern getFrontPattern(){
        return frontPattern;
    }

    public WindowPattern getRearPattern(){
        return rearPattern;
    }

    public String toString(){
        return new String(frontPattern + "\n" + rearPattern);
    }

    public static WindowPatternCard loadFromScanner(Scanner aScanner) throws Colour.ColorNotFoundException {

        WindowPattern f = WindowPattern.loadFromScanner(aScanner);
        aScanner.nextLine();
        WindowPattern r = WindowPattern.loadFromScanner(aScanner);
        return new WindowPatternCard(f.getTitle() + " - " + r.getTitle(),"",f, r );
    }
}
