package project.ing.soft;

import javafx.scene.paint.Color;
import project.ing.soft.model.Colour;

import java.util.HashMap;
import java.util.logging.Level;

public class Settings {
    private static Settings internalInstance;
    // General Game Settings
    private int nrPlayersOfNewMatch = 4;
    private int nrOfRound = 10;
    // Connection Settings
    private int port = 3000;
    private String host = "localhost";
    private String defaultIpForRMI = "127.0.0.1";
    // CLI settings
    private int TEXT_CARD_WIDTH = 29;
    private int TEXT_CARD_HEIGHT = 12;
    // GUI settings
    private int MATRIX_NR_ROW = 4;
    private int MATRIX_NR_COL = 5;
    private double MIN_SCREEN_SIZE = 1366;
    private int CELL_DIMENSION = 30;
    private int CARD_HEIGHT = 200;
    private Color BG_SCENE_COLOR = Color.BLACK;
    private HashMap<Colour, String> mapBgColour;

    private boolean GAME_START_TIMEOUT_ENABLED = true;
    private boolean TURN_TIMEOUT_ENABLED = true;
    private long TURN_TIMEOUT = 120000;
    private long GAME_START_TIMEOUT = 6000;
    private long SYNCH_TIME = 5000;

    private String tokenProperty      = "SagradaToken";
    private Level defaultLoggingLevel = Level.SEVERE;
    private String defaultRmiApName   = "accessPoint";

    private Settings() {
        mapBgColour = new HashMap<>();
        mapBgColour.put(Colour.BLUE, "#4286f4");
        mapBgColour.put(Colour.VIOLET, "#b762fc");
        mapBgColour.put(Colour.RED, "#fc5067");
        mapBgColour.put(Colour.GREEN, "#6af278");
        mapBgColour.put(Colour.YELLOW, "#f5f97a");
        mapBgColour.put(Colour.WHITE, "#ffffff");
    }

    public static Settings instance(){
        if (internalInstance == null)
            return new Settings();
        return internalInstance;
    }

    //region getter

    public long getSYNCH_TIME(){
        return SYNCH_TIME;
    }

    public boolean isTURN_TIMEOUT_ENABLED(){
        return TURN_TIMEOUT_ENABLED;
    }

    public boolean isGAME_START_TIMEOUT_ENABLED() {
        return GAME_START_TIMEOUT_ENABLED;
    }

    public long getTURN_TIMEOUT() {
        return TURN_TIMEOUT;
    }

    public long getGAME_START_TIMEOUT() {
        return GAME_START_TIMEOUT;
    }

    public int getNrPlayersOfNewMatch() {
        return nrPlayersOfNewMatch;
    }

    public int getNrOfRound() {
        return nrOfRound;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getDefaultIpForRMI() {
        return defaultIpForRMI;
    }

    public int getTEXT_CARD_WIDTH() {
        return TEXT_CARD_WIDTH;
    }

    public int getTEXT_CARD_HEIGHT() {
        return TEXT_CARD_HEIGHT;
    }

    public int getMATRIX_NR_ROW() {
        return MATRIX_NR_ROW;
    }

    public int getMATRIX_NR_COL() {
        return MATRIX_NR_COL;
    }

    public double getMIN_SCREEN_SIZE() {
        return MIN_SCREEN_SIZE;
    }

    public int getCELL_DIMENSION() {
        return CELL_DIMENSION;
    }

    public int getCARD_HEIGHT() {
        return CARD_HEIGHT;
    }

    public Color getBG_SCENE_COLOR() {
        return BG_SCENE_COLOR;
    }

    public HashMap<Colour, String> getMapBgColour(){
        return mapBgColour;
    }

    public Level getDefaultLoggingLevel() {
        return defaultLoggingLevel;
    }

    public String tokenProperty() {
        return tokenProperty;
    }

    public String getRmiApName() {
        return defaultRmiApName;
    }
    //endregion

    public enum ObjectivesProperties{

        ShadesOfRed("Shades Of Red", "Sum of values of red dice", "objectives/private/30%/objectives-13.png", 1),
        ShadesOfBlue("Shades Of Blue", "Sum of values of blue dice", "objectives/private/30%/objectives-16.png", 1),
        ShadesOfPurple("Shades Of Purple", "Sum of values of violet dice", "objectives/private/30%/objectives-17.png", 1),
        ShadesOfYellow("Shades Of Yellow", "Sum of values of yellow dice", "objectives/private/30%/objectives-14.png", 1),
        ShadesOfGreen("Shades Of Green", "Sum of values of green dice", "objectives/private/30%/objectives-15.png", 1),
        ColourVariety("Colour Variety", "Sets of pieces of each colour", "objectives/public/30%/objectives-11.png", 4),
        RowColourVariety("Row Colour Variety", "Rows with no repeated colours", "objectives/public/30%/objectives-2.png", 6),
        ColumnColourVariety("Column Colour Variety", "Columns with no repeated colours", "objectives/public/30%/objectives-3.png", 5),
        Diagonals("Diagonals", "2 or more pieces of the same colour placed on a diagonal", "objectives/public/30%/objectives-10.png", 1),
        ShadeVariety("Shade Variety", "Sets of pieces of each shade", "objectives/public/30%/objectives-9.png", 5),
        RowShadeVariety("Row Shade Variety", "Rows with no repeated shades", "objectives/public/30%/objectives-4.png", 5),
        ColumnShadeVariety("Column Shade Variety", "Columns with no repeated shades", "objectives/public/30%/objectives-5.png", 4),
        LightShades("Light Shades", "Sets of (1,2)", "objectives/public/30%/objectives-6.png", 2),
        MediumShades("Medium Shades", "Sets of (3,4)", "objectives/public/30%/objectives-7.png", 2),
        DarkShades("Dark Shades", "Sets of (5,6)", "objectives/public/30%/objectives-8.png", 2);

        private String title;
        private String description;
        private String path;
        private int points;

        ObjectivesProperties(String title, String description, String path, int points){
            this.title = title;
            this.description = description;
            this.path = path;
            this.points = points;
        }

        public String getTitle(){
            return title;
        }

        public String getDescription(){
            return description;
        }

        public String getPath(){
            return path;
        }

        public int getPoints(){
            return points;
        }
    }

}
