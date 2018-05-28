package project.ing.soft;

import javafx.scene.paint.Color;
import project.ing.soft.model.Colour;

import java.util.HashMap;

public class Settings {
    private static Settings internalInstance;
    // General Game Settings
    private int nrPlayersOfNewMatch = 2;
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
    private HashMap mapBgColour;

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

    public int getNrPlayersOfNewMatch() {
        return nrPlayersOfNewMatch;
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
    //endregion


}
