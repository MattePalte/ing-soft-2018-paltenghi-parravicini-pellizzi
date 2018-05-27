package project.ing.soft;

import javafx.scene.paint.Color;
import project.ing.soft.model.Colour;

import java.util.HashMap;

public class Settings {
    public static final int nrPlayersOfNewMatch = 2;
    public static final int port = 3000;
    public static final String host = "localhost";
    public static final String defaultIpForRMI = "127.0.0.1";

    public static final int TEXT_CARD_WIDTH = 29;
    public static final int TEXT_CARD_HEIGHT = 12;

    public static int MATRIX_NR_ROW = 4;
    public static int MATRIX_NR_COL = 5;
    public static double MIN_SCREEN_SIZE = 1366;

    public static int CELL_DIMENSION = 30;
    public static int CARD_HEIGHT = 200;

    public static Color BG_SCENE_COLOR = Color.BLACK;


    public static HashMap<Colour, String> getMapBgColour(){
        HashMap mapBgColour = new HashMap<>();
        mapBgColour.put(Colour.BLUE, "#4286f4");
        mapBgColour.put(Colour.VIOLET, "#b762fc");
        mapBgColour.put(Colour.RED, "#fc5067");
        mapBgColour.put(Colour.GREEN, "#6af278");
        mapBgColour.put(Colour.YELLOW, "#f5f97a");
        mapBgColour.put(Colour.WHITE, "#ffffff");
        return mapBgColour;
    }





}
