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
     * @param title of the pattern card
     * @param frontPattern front of the card
     * @param rearPattern rear if the card
     * @throws GameInvalidException
     */
    private WindowPatternCard(String title, WindowPattern frontPattern, WindowPattern rearPattern) throws GameInvalidException {
        this.title          = title;
        this.frontPattern   = frontPattern;
        this.rearPattern    = rearPattern;

        // enable this check if you want to allow only pattern card with the same dimension on both sides
        /*if (!(this.frontPattern.getWidth() == this.rearPattern.getWidth() &&
              this.frontPattern.getHeight() == this.rearPattern.getHeight())){
            throw new GameInvalidException("WindowPatternCard error: Pattern cards faces with incompatible dimensions");
        }*/
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

    /**
     * Given a scanner it returns a pattern card created
     * starting from the beginning of the scanner. It creates both the
     * front and the rear
     * @param aScanner of the file
     * @return a pattern card
     * @throws Colour.ColorNotFoundException if the encoding of a color is incorrect
     * @throws GameInvalidException if game is not valid
     */
    public static WindowPatternCard loadAPatternCardFromScanner(Scanner aScanner) throws Colour.ColorNotFoundException, GameInvalidException {

        WindowPattern f = WindowPattern.loadFromScanner(aScanner);
        aScanner.nextLine();
        WindowPattern r = WindowPattern.loadFromScanner(aScanner);
        return new WindowPatternCard(f.getTitle() + " - " + r.getTitle(),f, r );
    }

    /**
     * Given a filepath it returns a list of pattern cards created from the
     * encoding in the txt file
     * @param pathname txt file with the encoded pattern cards
     * @return list of pattern cards objects
     */
    public static List<WindowPatternCard> loadFromFile(String pathname) {
        ArrayList<WindowPatternCard> patterns = new ArrayList<>();
        try( Scanner input = new Scanner(WindowPatternCard.class.getResourceAsStream(pathname))) {
            int nrOfCouple = input.nextInt();
            input.nextLine();
            for (int i = 0; i < nrOfCouple; i++) {
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
