package project.ing.soft.cards;

import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.Colour;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class WindowPatternCard extends Card implements Serializable {
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

    @Override
    public String toString(){
        return frontPattern + "\n" + rearPattern;
    }

    public static WindowPatternCard loadFromScanner(Scanner aScanner) throws Colour.ColorNotFoundException, GameInvalidException {

        WindowPattern f = WindowPattern.loadFromScanner(aScanner);
        aScanner.nextLine();
        WindowPattern r = WindowPattern.loadFromScanner(aScanner);
        return new WindowPatternCard(f.getTitle() + " - " + r.getTitle(),"",f, r );
    }

    public static ArrayList<WindowPatternCard> loadFromFile(String pathname) {
        ArrayList<WindowPatternCard> patterns = new ArrayList<>();
        try( Scanner input = new Scanner(new File(pathname))) {
            for (int i = 0; i < 12; i++) {
                patterns.add(WindowPatternCard.loadFromScanner(input));
                input.nextLine();
            }

        } catch(Exception ex){
            System.out.println("Error while loading window pattern cards from file");
            patterns = new ArrayList<>();
        }

        return patterns;
    }
}
