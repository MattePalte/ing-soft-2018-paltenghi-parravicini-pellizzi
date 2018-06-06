package project.ing.soft.model.cards;

import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.model.Colour;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WindowPatternCard implements Serializable {
    private String        title;
    private WindowPattern frontPattern;
    private WindowPattern rearPattern;

    /**
     * requires title != null && description != null && frontPattern != null && rearPattern != null &&
     *          rearPattern != frontPattern && rearPattern.toString() != frontPattern.toString()
     * @param title
     * @param frontPattern
     * @param rearPattern
     * @throws GameInvalidException
     */
    private WindowPatternCard(String title, WindowPattern frontPattern, WindowPattern rearPattern) throws GameInvalidException {
        this.title          = title;
        this.frontPattern   = frontPattern;
        this.rearPattern    = rearPattern;

        if (!(this.frontPattern.getWidth() == this.rearPattern.getWidth() &&
              this.frontPattern.getHeight() == this.rearPattern.getHeight())){
            throw new GameInvalidException("WindowPatternCard error: Pattern cards faces with incompatible dimensions");
        }
    }

    public String getTitle(){
        return title;
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

    public static WindowPatternCard loadAPatternCardFromScanner(Scanner aScanner) throws Colour.ColorNotFoundException, GameInvalidException {

        WindowPattern f = WindowPattern.loadFromScanner(aScanner);
        aScanner.nextLine();
        WindowPattern r = WindowPattern.loadFromScanner(aScanner);
        return new WindowPatternCard(f.getTitle() + " - " + r.getTitle(),f, r );
    }

    public static List<WindowPatternCard> loadFromFile(String pathname) {
        ArrayList<WindowPatternCard> patterns = new ArrayList<>();
        try( Scanner input = new Scanner(WindowPatternCard.class.getResourceAsStream(pathname))) {
            for (int i = 0; i < 12; i++) {
                patterns.add(WindowPatternCard.loadAPatternCardFromScanner(input));
                input.nextLine();
            }

        } catch(Exception ex){
            //"Error while loading window pattern cards from file"
            patterns = new ArrayList<>();
        }

        return patterns;
    }

    public static List<WindowPatternCard> loadFromFile(URL pathname) {
        return loadFromFile(pathname.getFile().replace("%20", " "));
    }


}
