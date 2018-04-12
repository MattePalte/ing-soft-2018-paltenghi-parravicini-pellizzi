package projectIngSoft;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Colour {


    RED   ("\u001B[31m","\u001B[41m"),
    YELLOW("\u001B[33m", "\u001B[47m"),
    GREEN ("\u001B[38m", "\u001B[42m"),
    BLUE  ("\u001B[34m", "\u001B[44m"),
    VIOLET("\u001B[35m", "\u001B[45m"),
    WHITE("\u001B[30m", "\u001B[107m");

    private static final String ANSI_RESET = "\u001B[0m";
    private static List<Colour> validColours;
    //https://stackoverflow.com/questions/2420389/static-initialization-blocks
    static{
        validColours = new ArrayList(Arrays.asList(values()));
        validColours.remove(WHITE);
    }
    /*
    https://en.wikipedia.org/wiki/ANSI_escape_code
    public static final String ANSI_BLACK="\u001B[30m";
    public static final String ANSI_RED="\u001B[31m";
    public static final String ANSI_GREEN="\u001B[32m";
    public static final String ANSI_YELLOW="\u001B[33m";
    public static final String ANSI_BLUE="\u001b[34m";
    public static final String ANSI_PURPLE="\u001b[35m";
    public static final String ANSI_CYAN="\u001b[36m";
    public static final String ANSI_WHITE="\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND="\u001B[40m";
    public static final String ANSI_RED_BACKGROUND="\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND="\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND="\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND="\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND="\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND="\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND="\u001B[47m";
     */

    private String ansiForegroundColour, ansiBackgroundColour;

    Colour( String aForegroundColour, String aBackgroundColour){
        this.ansiBackgroundColour = aBackgroundColour;
        this.ansiForegroundColour = aForegroundColour;
    }

    public String ColourBackground(String aString){
        return ansiBackgroundColour+aString+ANSI_RESET;
    }

    public String ColourForeground(String aString) {
        return ansiForegroundColour + aString + ANSI_RESET;
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
        public ColorNotFoundException(String aString){
            super(aString);
        }
    }

}
