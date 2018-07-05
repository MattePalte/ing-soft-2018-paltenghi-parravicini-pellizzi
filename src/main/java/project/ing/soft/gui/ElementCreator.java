package project.ing.soft.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.WindowPattern;

/**
 * Class used to create custom elements for the GUI such as:
 * - pattern cards
 * - toolcards
 * - objective cards
 * It relies only on static methods.
 */

class ElementCreator {

    private ElementCreator() {
    }

    /**
     * Method to create a patter card as a grid pane,
     * it is used during the initial phase of the match to choose the
     * pattern card.
     * @param pattern to be drawn/represented
     * @param cellDimension dimension of the cell
     * @return gridPane representing the pattern card
     */
    static GridPane createPattern(WindowPattern pattern, int cellDimension) {
        // Create Grid for matrix of this pattern
        GridPane gPane = new GridPane();
        if (pattern == null) return gPane;
        gPane.setPadding(new Insets(7, 7, 7, 7));
        gPane.setVgap(10);
        gPane.setHgap(10);
        gPane.setAlignment(Pos.CENTER);
        // Create cells
        for (int row = 0; row < pattern.getHeight(); row++) {
            for (int col = 0; col < pattern.getWidth(); col++) {
                Constraint constraint = pattern.getConstraintsMatrix()[row][col];
                ImageView bg;
                if (constraint != null && constraint.getImgPath() != "") {
                    Image image = new Image(constraint.getImgPath());
                    bg = new ImageView(image);
                    bg.setFitHeight(cellDimension);
                    bg.setFitWidth(cellDimension);
                    bg.setPreserveRatio(true);
                    bg.setSmooth(true);
                    bg.setCache(true);
                } else {
                    bg = new ImageView();
                    bg.setFitHeight(cellDimension);
                    bg.setFitWidth(cellDimension);
                }
                if (constraint != null){
                    StackPane pane = new StackPane();
                    pane.setStyle("-fx-background-color:" + constraint.getColour().getWebColor());
                    gPane.add(pane, col, row);
                }
                gPane.add(bg, col, row);
            }
        }
        return gPane;
    }

    /**
     * Method to create a generic imageview containing a card.
     * The card can be an objective card or a toolcard, in both cases
     * the image path is retrieved from the getPath method of the card class.
     * THe image aumatically resize itself depending on the screen dimension.
     * @param card to be shown
     * @param stage to get the height of the current window
     * @return the ImageView containing the image of the card
     */
    static ImageView createCard(Card card, Stage stage){
        Image img = new Image(card.getImgPath());
        ImageView iv = new ImageView(img);
        iv.setImage(img);
        iv.fitHeightProperty().bind(stage.heightProperty().multiply(0.25));
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        Tooltip.install(iv, new Tooltip(
                card.getTitle() + "\n" +
                        card.getDescription()));
        return iv;
    }

    /**
     * It relies on the createPattern method to draw the basic pattern
     * then it added dice on top of it. The dice passed should be those belonging
     * to that specific player.
     * @param pattern to be drawn/represented
     * @param placedDie die to be printed on the pattern card
     * @param cellDimension dimension of the cell
     * @return gridPane representing the pattern card
     */
    static GridPane createPattern(WindowPattern pattern, Die[][] placedDie, int cellDimension){
        GridPane pane = createPattern(pattern, cellDimension);
        if (pattern == null) return pane;
        // Create cells
        for (int row = 0; row < pattern.getHeight(); row++) {
            for (int col = 0; col < pattern.getWidth(); col++) {
                if (placedDie[row][col] == null) continue;
                Die currentDie = placedDie[row][col];
                Image image = new Image(currentDie.getImgPath());
                ImageView bg = new ImageView(image);
                bg.setFitHeight(cellDimension);
                bg.setFitWidth(cellDimension);
                bg.setPreserveRatio(true);
                bg.setSmooth(true);
                bg.setCache(true);
                pane.add(bg, col, row);
            }
        }
        return pane;
    }

    /**
     * It creates a pattern card with buttons with specific IDs. In this way
     * the main tread can access them and set On click listener on them.
     * @param pattern to be drawn/represented
     * @param placedDie die to be printed on the pattern card
     * @param cellDimension dimension of the cell
     * @param prefixTag that will be added to the ID of each button
     * @return gridPane representing the pattern card
     */
    static GridPane createClickablePattern(WindowPattern pattern, Die[][] placedDie, int cellDimension, String prefixTag){
        // Create Grid for matrix of this pattern
        GridPane gPane = new GridPane();
        gPane.setVgap(10);
        gPane.setHgap(10);
        gPane.setAlignment(Pos.CENTER);
        Constraint[][] constraints = pattern.getConstraintsMatrix();
        // Create cells
        for (int row = 0; row < pattern.getHeight(); row++) {
            for (int col = 0; col < pattern.getWidth(); col++) {
                Constraint constraint = constraints[row][col];
                Button currentCell = new Button();
                currentCell.setId(prefixTag + row + col);
                ImageView bg;
                if (constraint != null && constraint.getImgPath() != "") {
                    Image image = new Image(constraint.getImgPath());
                    bg = new ImageView(image);
                    bg.setFitHeight(cellDimension);
                    bg.setFitWidth(cellDimension);
                    bg.setPreserveRatio(true);
                    bg.setSmooth(true);
                    bg.setCache(true);
                } else {
                    bg = new ImageView();
                    bg.setFitHeight(cellDimension);
                    bg.setFitWidth(cellDimension);
                }
                if (constraint != null){
                    currentCell.setStyle("-fx-background-color:" + constraint.getColour().getWebColor());
                }
                if (placedDie[row][col] != null) {
                    Die currentDie = placedDie[row][col];
                    Image image = new Image(currentDie.getImgPath());
                    bg = new ImageView(image);
                    bg.setFitHeight(cellDimension);
                    bg.setFitWidth(cellDimension);
                    bg.setPreserveRatio(true);
                    bg.setSmooth(true);
                    bg.setCache(true);
                }
                currentCell.setGraphic(bg);
                gPane.add(currentCell, col, row);
            }
        }
        return gPane;
    }
}
