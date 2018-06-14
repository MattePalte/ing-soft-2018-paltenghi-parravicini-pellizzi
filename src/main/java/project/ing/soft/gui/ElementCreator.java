package project.ing.soft.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.WindowPattern;

class ElementCreator {

    private ElementCreator() {
    }

    static GridPane createPattern(WindowPattern pattern, int cellDimension) {
        // Create Grid for matrix of this pattern
        GridPane gPane = new GridPane();
        gPane.setPadding(new Insets(7, 7, 7, 7));
        gPane.setVgap(10);
        gPane.setHgap(10);
        gPane.setAlignment(Pos.CENTER);
        // Create cells
        for (int row = 0; row < Settings.instance().getMATRIX_NR_ROW(); row++) {
            for (int col = 0; col < Settings.instance().getMATRIX_NR_COL(); col++) {
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

    static ImageView createCard(Card card, Stage stage){
        Image img = new Image(card.getImgPath());
        ImageView iv = new ImageView(img);
        iv.setImage(img);
        //iv.setFitHeight(height);
        iv.fitHeightProperty().bind(stage.heightProperty().multiply(0.25));
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        Tooltip.install(iv, new Tooltip(
                card.getTitle() + "\n" +
                        card.getDescription()));
        return iv;
    }

    static GridPane createPattern(WindowPattern pattern, Die[][] placedDie, int cellDimension){
        GridPane pane = createPattern(pattern, cellDimension);
        // Create cells
        for (int row = 0; row < Settings.instance().getMATRIX_NR_ROW(); row++) {
            for (int col = 0; col < Settings.instance().getMATRIX_NR_COL(); col++) {
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

    static GridPane createClickablePattern(WindowPattern pattern, Die[][] placedDie, int cellDimension, String prefixTag){
        // Create Grid for matrix of this pattern
        GridPane gPane = new GridPane();
        gPane.setVgap(10);
        gPane.setHgap(10);
        gPane.setAlignment(Pos.CENTER);
        Constraint[][] constraints = pattern.getConstraintsMatrix();
        // Create cells
        for (int row = 0; row < Settings.instance().getMATRIX_NR_ROW(); row++) {
            for (int col = 0; col < Settings.instance().getMATRIX_NR_COL(); col++) {
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
