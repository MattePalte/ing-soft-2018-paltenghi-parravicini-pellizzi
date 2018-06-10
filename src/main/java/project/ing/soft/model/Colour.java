package project.ing.soft.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    BLUE  ("\u001B[96m", "\u001B[44m", "#4286f4"),
    GREEN ("\u001B[92m", "\u001B[42m", "#6af278"),
    RED   ("\u001B[91m","\u001B[41m", "#fc5067"),
    VIOLET("\u001B[95m", "\u001B[45m", "#b762fc"),
    YELLOW("\u001B[93m", "\u001B[43m", "#f5f97a"),

    WHITE("\u001B[97m", "\u001B[47m", "#ffffff");

    private static final String ANSI_RESET = "\u001B[0m";
    private static List<Colour> validColours;
    //https://stackoverflow.com/questions/2420389/static-initialization-blocks
    static{
        validColours = new ArrayList<>(Arrays.asList(values()));
        validColours.remove(WHITE);
    }


    private String ansiForegroundColour;
    private String ansiBackgroundColour;
    private String webColor;

    Colour( String aForegroundColour, String aBackgroundColour, String webColor){
        this.ansiBackgroundColour = aBackgroundColour;
        this.ansiForegroundColour = aForegroundColour;
        this.webColor = webColor;
    }

    public String colourBackground(String aString){
        return ansiBackgroundColour+aString+ANSI_RESET;
    }

    public String colourForeground(String aString) {
        return ansiForegroundColour + aString + ANSI_RESET;
    }

    public String getWebColor() {
        return webColor;
    }

    @Override
    public String toString() {
        return "Colour{" + name()+ "}";
    }

    public static List<Colour> validColours(){
        return validColours;
    }

    public static Colour valueOf( char aChar) throws ColorNotFoundException{
       for (Colour c: values())
           if (c.name().charAt(0) == aChar )
               return c;
       throw new ColorNotFoundException("Color not found for abbreviation "+ aChar);
    }

    public static class ColorNotFoundException extends Exception{
        ColorNotFoundException(String aString){
            super(aString);
        }
    }

}
