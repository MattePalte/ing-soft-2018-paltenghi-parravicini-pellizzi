package project.ing.soft.model;

import project.ing.soft.Settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of possible colours used in the game
 */
public enum Colour implements Serializable{
    //w.r.t https://en.wikipedia.org/wiki/ANSI_escape_code
    //To avoid problems related to width of different character
    //Sun's Glory ******
    //        ⚀   ⚃|
    //        ⚅    |
    //        ⚄⚂   |
    //        ⚄⚃⚁⚀ |
    //https://en.wikipedia.org/wiki/Halfwidth_and_fullwidth_forms
    //https://en.wikipedia.org/wiki/Whitespace_character
    //http://jkorpela.fi/chars/spaces.html
    // Because ⚄ is an irregular width character -> 1+1/3 em

    BLUE  (Settings.instance().isDeploy() ? "\u001B[38;2;1;111;187m"  : "\u001B[96m", Settings.instance().isDeploy() ? "\u001B[48;2;136;184;301m"   : "\u001B[44m", "#4286f4"),
    GREEN (Settings.instance().isDeploy() ? "\u001B[38;2;1;187;0m"    : "\u001B[92m", Settings.instance().isDeploy() ? "\u001B[48;2;178;202;45m"    : "\u001B[42m", "#6af278"),
    RED   ("\u001B[91m","\u001B[41m", "#fc5067"),
    VIOLET(Settings.instance().isDeploy() ? "\u001B[38;2;174;138;190m": "\u001B[95m", Settings.instance().isDeploy() ? "\u001B[48;2;184;148;200m"   : "\u001B[45m", "#b762fc"),
    YELLOW(Settings.instance().isDeploy() ? "\u001B[38;2;194;171;33m" : "\u001B[93m", Settings.instance().isDeploy() ? "\u001B[48;2;214;191;85m"    : "\u001B[43m", "#f5f97a"),
    WHITE (Settings.instance().isDeploy() ? "\u001B[38;2;0;0;0m"      : "\u001B[97m", Settings.instance().isDeploy() ? "\u001B[48;2;153;153;153m"   : "\u001B[47m", "#ffffff");

    private static final String ANSI_RESET = "\u001B[0m";
    private static final List<Colour> validColours;
    //https://stackoverflow.com/questions/2420389/static-initialization-blocks
    static{
        validColours = new ArrayList<>(Arrays.asList(values()));
        validColours.remove(WHITE);
    }


    private String ansiForegroundColour;
    private String ansiBackgroundColour;
    private String webColor;

    /**
     * Default constructor for colours in the enumeration
     * @param aForegroundColour ANSI code for console's foreground colour
     * @param aBackgroundColour ANSI code for console's background colour
     * @param webColor Hexadecimal code for text color
     */
    Colour( String aForegroundColour, String aBackgroundColour, String webColor){
        this.ansiBackgroundColour = aBackgroundColour;
        this.ansiForegroundColour = aForegroundColour;
        this.webColor = webColor;
    }

    /**
     * Method which returns a String which indicates to print the chosen String on a coloured
     * background. Background's colour is chosen by calling the method on the chosen Colour
     * @param aString the string that is asked to be printed on the coloured background
     * @return the string passed as a parameter, wrapped between the chosen colour ANSI background code
     * and ANSI_RESET code
     */
    public String colourBackground(String aString){
        return ansiBackgroundColour+aString+ANSI_RESET;
    }

    /**
     * Method which returns a String which indicates to print the chosen String colouring console's text.
     * Text's colour is chosen by calling the method on the chosen Colour
     * @param aString the string that is asked to be coloured
     * @return the string passed as a parameter, wrapped between the chosen colour ANSI foreground code
     * and ANSI_RESET code
     */
    public String colourForeground(String aString) {
        return ansiForegroundColour + aString + ANSI_RESET;
    }

    /**
     *
     * @return the hexadecimal code of the colour
     */
    public String getWebColor() {
        return webColor;
    }

    /**
     *
     * @return a String representation of the colour
     */
    @Override
    public String toString() {
        return "Colour{" + name()+ "}";
    }

    /**
     * Static method:
     * @return a list of the valid colours used in the game
     */
    public static List<Colour> validColours(){
        return validColours;
    }

    /**
     * Method used to return a colour according to an abbreviation of the colour itself
     * @param aChar abbreviation of a colour name
     * @return the colour corresponding to the given abbreviation
     * @throws ColorNotFoundException if the abbreviation given does not correspond to any colour
     */
    public static Colour valueOf( char aChar) throws ColorNotFoundException{
       for (Colour c: values())
           if (c.name().charAt(0) == aChar )
               return c;
       throw new ColorNotFoundException("Color not found for abbreviation "+ aChar);
    }

    /**
     * Exception used to notify that the asked colour does not exist in the enumeration
     */
    public static class ColorNotFoundException extends Exception{
        /**
         * ColourNotFoundException default constructor
         * @param aString the string to set as an Exception message
         */
        ColorNotFoundException(String aString){
            super(aString);
        }
    }

}
