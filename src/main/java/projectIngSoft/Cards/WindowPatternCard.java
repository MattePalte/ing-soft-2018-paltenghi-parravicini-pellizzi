package projectIngSoft.Cards;

import projectIngSoft.Colour;
import projectIngSoft.Cards.WindowPattern;

import java.util.Scanner;

public class WindowPatternCard extends Card {
    private WindowPattern frontPattern;
    private WindowPattern rearPattern;
    private Boolean isFlipped = false;

    // requires title != null && description != null && frontPattern != null && rearPattern != null &&
    //          rearPattern != frontPattern && rearPattern.toString() != frontPattern.toString()
    private WindowPatternCard(String title, String description, WindowPattern frontPattern, WindowPattern rearPattern) {
        super(title, description);
        this.frontPattern = frontPattern;
        this.rearPattern = rearPattern;
    }

    // ensure [! /old(toString()).equals(toString()) ]
    public void flip(){
        isFlipped = !isFlipped;
    }

    public WindowPattern getCurrentPattern() {
        return isFlipped ? rearPattern : frontPattern;
    }

    @Override
    public String toString() {
        return isFlipped ? rearPattern.toString() : frontPattern.toString();
    }

    public static WindowPatternCard loadFromScanner(Scanner aScanner) throws Colour.ColorNotFoundException {

        WindowPattern f = new WindowPattern(aScanner);
        WindowPattern r = new WindowPattern(aScanner);

        return new WindowPatternCard(f.getTitle() + " - " + r.getTitle(),"",f, r );
    }
}
