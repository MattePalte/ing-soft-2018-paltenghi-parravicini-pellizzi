package project.ing.soft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private int textCardWidth;
    private int textCardHeight;
    // GUI settings
    private int matrixNrRow;
    private int matrixNrCol;
    private double minScreenSize;
    private int cellDimension;
    private int cardHeight;
    private Color bgSceneColor;

    private boolean gameStartTimeoutEnabled;
    private boolean turnTimeoutEnabled;
    private long turnTimeout;
    private long gameStartTimeout;
    private long synchTime;

    private String tokenProperty;
    private Level defaultLoggingLevel;
    private String defaultRmiApName;
    private boolean deploy;

    private final Logger log = Logger.getLogger(Objects.toString(this));

    private Settings() {
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
            internalInstance = gson.fromJson(sb.toString(), Settings.class);
        }
        return internalInstance;
    }

    public void save(String name){

        // methods to create file of backup settings when changing
        // some attributes in this class
        PrintWriter writer = null;
        try {
            writer = new PrintWriter( Settings.class.getResource(name).getFile());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.println(gson.toJson(this));
            writer.close();
        } catch (IOException e) {
            log.log(Level.INFO, Arrays.toString(e.getStackTrace()));
        } finally {
            if (writer != null) writer.close();
        }

    }

    public String getProperty(String key){
        if(properties!= null)
            return properties.get(key);
        return null;
    }

    public void setProperty(String name, String value){
        if(properties== null)
            properties = new HashMap<>();
        properties.put(name, value);
    }

    private Map<String,String> properties;
    //region getter

    public long getSynchTime(){
        return synchTime;
    }

    public boolean isTurnTimeoutEnabled(){
        return turnTimeoutEnabled;
    }

    public boolean isGameStartTimeoutEnabled() {
        return gameStartTimeoutEnabled;
    }

    public long getTurnTimeout() {
        return turnTimeout;
    }

    public long getGameStartTimeout() {
        return gameStartTimeout;
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

    public int getTextCardWidth() {
        return textCardWidth;
    }

    public int getTextCardHeight() {
        return textCardHeight;
    }

    public int getMatrixNrRow() {
        return matrixNrRow;
    }

    public int getMatrixNrCol() {
        return matrixNrCol;
    }

    public double getMinScreenSize() {
        return minScreenSize;
    }

    public int getCellDimension() {
        return cellDimension;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    public Color getBgSceneColor() {
        return bgSceneColor;
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

    public boolean isDeploy(){ return deploy;}
    //endregion

    public enum ObjectivesProperties{

        SHADES_OF_RED("Shades Of Red", "Sum of values of red dice", "objectives/private/30%/objectives-13.png", 1),
        SHADES_OF_BLUE("Shades Of Blue", "Sum of values of blue dice", "objectives/private/30%/objectives-16.png", 1),
        SHADES_OF_PURPLE("Shades Of Purple", "Sum of values of violet dice", "objectives/private/30%/objectives-17.png", 1),
        SHADES_OF_YELLOW("Shades Of Yellow", "Sum of values of yellow dice", "objectives/private/30%/objectives-14.png", 1),
        SHADES_OF_GREEN("Shades Of Green", "Sum of values of green dice", "objectives/private/30%/objectives-15.png", 1),
        COLOUR_VARIETY("Colour Variety", "Sets of pieces of each colour", "objectives/public/30%/objectives-11.png", 4),
        ROW_COLOUR_VARIETY("Row Colour Variety", "Rows with no repeated colours", "objectives/public/30%/objectives-2.png", 6),
        COLUMN_COLOUR_VARIETY("Column Colour Variety", "Columns with no repeated colours", "objectives/public/30%/objectives-3.png", 5),
        DIAGONALS("Diagonals", "2 or more pieces of the same colour placed on a diagonal", "objectives/public/30%/objectives-10.png", 1),
        SHADE_VARIETY("Shade Variety", "Sets of pieces of each shade", "objectives/public/30%/objectives-9.png", 5),
        ROW_SHADE_VARIETY("Row Shade Variety", "Rows with no repeated shades", "objectives/public/30%/objectives-4.png", 5),
        COLUMN_SHADE_VARIETY("Column Shade Variety", "Columns with no repeated shades", "objectives/public/30%/objectives-5.png", 4),
        LIGHT_SHADES("Light Shades", "Sets of (1,2)", "objectives/public/30%/objectives-6.png", 2),
        MEDIUM_SHADES("Medium Shades", "Sets of (3,4)", "objectives/public/30%/objectives-7.png", 2),
        DARK_SHADES("Dark Shades", "Sets of (5,6)", "objectives/public/30%/objectives-8.png", 2);

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

    public void setDefaultLoggingLevel(Level lvl){
        defaultLoggingLevel = lvl;
    }


}
