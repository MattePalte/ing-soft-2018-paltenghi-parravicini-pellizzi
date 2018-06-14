package project.ing.soft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;

public class Settings {
    private static Settings internalInstance;
    // General Game Settings
    private int nrPlayersOfNewMatch;
    private int nrOfRound;
    // Connection Settings
    private int port;
    private String host;
    private String defaultIpForRMI;
    // CLI settings
    private int TEXT_CARD_WIDTH;
    private int TEXT_CARD_HEIGHT;
    // GUI settings
    private int MATRIX_NR_ROW;
    private int MATRIX_NR_COL;
    private double MIN_SCREEN_SIZE;
    private int CELL_DIMENSION;
    private int CARD_HEIGHT;
    private Color BG_SCENE_COLOR;

    private boolean GAME_START_TIMEOUT_ENABLED;
    private boolean TURN_TIMEOUT_ENABLED;
    private long TURN_TIMEOUT;
    private long GAME_START_TIMEOUT;
    private long SYNCH_TIME ;

    private String tokenProperty;
    private Level defaultLoggingLevel;
    private String defaultRmiApName;

    private Settings() {
        // methods to create file of backup settings when changing
        // some attributes in this class
//        PrintWriter writer = null;
//        try {
//            String path = "C:\\Users\\Matteo\\Desktop\\settings.json";
//            File f = new File(path);
//            writer = new PrintWriter(f);
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            writer.println(gson.toJson(this));
//            System.out.println("Successfully Copied JSON Object to File...");
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (writer != null) writer.close();
//        }
    }

    public static Settings instance(){
        if (internalInstance == null) {
//          read from file
            Gson gson = new Gson();
            InputStream inputStream = Settings.class.getResourceAsStream("/settings.json");
            Scanner scanner = new Scanner(inputStream);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            //System.out.println("Settings content -> " + sb.toString());
            internalInstance = gson.fromJson(sb.toString(), Settings.class);
            //System.out.println("Settings loaded -> " + internalInstance);
        }
        return internalInstance;
    }

    //region getter

    public long getSYNCH_TIME(){
        return SYNCH_TIME;
    }

    public boolean isTURN_TIMEOUT_ENABLED(){
        return TURN_TIMEOUT_ENABLED;
    }

    public boolean isGameStartTimeoutEnabled() {
        return GAME_START_TIMEOUT_ENABLED;
    }

    public long getTURN_TIMEOUT() {
        return TURN_TIMEOUT;
    }

    public long getGameStartTimeout() {
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

    public double getMinScreenSize() {
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

    public Level getDefaultLoggingLevel() {
        return defaultLoggingLevel;
    }

    public String tokenProperty() {
        return tokenProperty;
    }

    public String getRemoteRmiApName() {
        StringBuilder sb = new StringBuilder("rmi://");
        sb.append(defaultIpForRMI);
        sb.append(":");
        sb.append(1099);
        sb.append("/");
        sb.append(defaultRmiApName);
        return new String(sb);
    }
    public String getRmiApName(){
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
