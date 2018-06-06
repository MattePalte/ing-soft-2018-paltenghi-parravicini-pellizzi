package project.ing.soft.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StyleBooster {

    private StyleBooster() {
    }

    public static void forPatternCard(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #a0a0a0;\n" +
                "       -fx-background-radius: 30;\n" +
                "       -fx-border-radius: 30;\n" +
                "       -fx-border-width:5;\n" +
                "       -fx-border-color: #424242;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }
    public static void forObjectiveCard(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #a0a0a0;\n"+
                "       -fx-border-width:5;\n"+
                "       -fx-border-color: #424242;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }
    public static void forToolCardCard(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #fff5bf;\n" +
                "       -fx-border-width:5;\n" +
                "       -fx-border-color: #9e9252;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }
    public static void forInstructionBox(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #fff5bf;\n" +
                "       -fx-border-width:5;\n" +
                "       -fx-border-color: #9e9252;\n" +
                "       -fx-font-size: 20px;\n" +
                "       -fx-font-fill: #9e9252;\n" +
                "       -fx-font-family: cursive;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }

}
