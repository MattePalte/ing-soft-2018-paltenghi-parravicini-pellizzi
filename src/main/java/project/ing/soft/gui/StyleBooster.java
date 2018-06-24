package project.ing.soft.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;

class StyleBooster {

    /**
     * Class used to style elements of the GUI.
     * It was introduced to resolve some lag derived from the use
     * of css stylesheet directly embedded into FXML.
     * It has only static methods.
     */
    private StyleBooster() {
    }

    /**
     * Method to style pattern Card
     * @param pane pane containing the pattern card
     * @param padding desired internal padding
     */
    static void forPatternCard(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #a0a0a0;\n" +
                "       -fx-background-radius: 30;\n" +
                "       -fx-border-radius: 30;\n" +
                "       -fx-border-width:5;\n" +
                "       -fx-border-color: #424242;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }

    /**
     * Method to style Objective Card
     * @param pane pane containing the Objective card
     * @param padding desired internal padding
     */
    static void forObjectiveCard(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #a0a0a0;\n"+
                "       -fx-border-width:5;\n"+
                "       -fx-border-color: #424242;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }
    /**
     * Method to style Toolcard Card
     * @param pane pane containing the Toolcard card
     * @param padding desired internal padding
     */
    static void forToolCardCard(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #fff5bf;\n" +
                "       -fx-border-width:5;\n" +
                "       -fx-border-color: #9e9252;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }
    /**
     * Method to style Instruction Box
     * @param pane pane containing the Instruction text box
     * @param padding desired internal padding
     */
    static void forInstructionBox(Pane pane, int padding) {
        String style =
                "       -fx-background-color: #19e4ff;\n" +
                "       -fx-border-width:5;\n" +
                "       -fx-border-color: #424242;\n" +
                "       -fx-font-size: 20px;\n" +
                "       -fx-font-fill: #9e9252;\n" +
                "       -fx-font-family: cursive;";
        pane.setStyle(style);
        pane.setPadding(new Insets(padding));
    }

}
