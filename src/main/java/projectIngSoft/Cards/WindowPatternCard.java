package projectIngSoft.Cards;

import projectIngSoft.Colour;
import projectIngSoft.exceptions.GameInvalidException;

import java.util.Scanner;

public class WindowPatternCard extends Card {
    private WindowPattern frontPattern;
    private WindowPattern rearPattern;

    // requires title != null && description != null && frontPattern != null && rearPattern != null &&
    //          rearPattern != frontPattern && rearPattern.toString() != frontPattern.toString()
    private WindowPatternCard(String title, String description, WindowPattern frontPattern, WindowPattern rearPattern) throws GameInvalidException {
        super(title, description);
        this.frontPattern = frontPattern;
        this.rearPattern = rearPattern;

        if (!(this.frontPattern.getWidth() == this.rearPattern.getWidth() &&
              this.frontPattern.getHeight() == this.rearPattern.getHeight())){
            throw new GameInvalidException("WindowPatternCard error: Pattern cards faces with incompatible dimensions");
        }
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

    public static WindowPatternCard loadFromScanner(Scanner aScanner) throws Colour.ColorNotFoundException, GameInvalidException {

        WindowPattern f = WindowPattern.loadFromScanner(aScanner);
        aScanner.nextLine();
        WindowPattern r = WindowPattern.loadFromScanner(aScanner);
        return new WindowPatternCard(f.getTitle() + " - " + r.getTitle(),"",f, r );
    }
}
