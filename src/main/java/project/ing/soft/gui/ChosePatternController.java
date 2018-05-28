package project.ing.soft.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import project.ing.soft.Settings;
import project.ing.soft.controller.IController;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;
import project.ing.soft.model.gamemanager.events.PatternCardDistributedEvent;

import java.util.ArrayList;
import java.util.List;


public class ChosePatternController {

    private IController gameController;
    private String nick;
    private Stage stage;
    private PatternCardDistributedEvent event;
    private PrivateObjective privObj;

    @FXML private Text txtTitle;
    @FXML private VBox frontFirst;
    @FXML private VBox rearFirst;
    @FXML private VBox frontSecond;
    @FXML private VBox rearSecond;
    @FXML private ImageView imgPrivateObjective;

    public void setGameController(IController gameController){
        this.gameController = gameController;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setPatternEvent(PatternCardDistributedEvent event) {
        this.event = event;
    }

    public void setPrivObj(PrivateObjective privObj) {
        this.privObj = privObj;
    }

    /**
     * General method to render 4 possible pattern and display private objective.
     * It should be called by the creator of this scene.
     * It add to the title the nick name of current player
     */
    public void renderThings(){
        /*Image img = new Image("gui/sagrada_small_splash.png");
        ivSplash.setImage(img);
        ivSplash.setFitWidth(startWidth);
        ivSplash.setFitHeight(startHeight);*/
        txtTitle.setText(nick + " choose a pattern");
        populate(frontFirst, event.getOne().getFrontPattern());
        populate(rearFirst, event.getOne().getRearPattern());
        populate(frontSecond, event.getTwo().getFrontPattern());
        populate(rearSecond, event.getTwo().getRearPattern());
        displayObjective();
    }

    /**
     * Get pattern card and side from pattern
     * and then send our chosen card to the game controller
     * @param pattern
     */
    private void chooseThis(WindowPattern pattern) {
        try {
            List<WindowPatternCard> possiblePatternCard = new ArrayList<>();
            possiblePatternCard.add(event.getOne());
            possiblePatternCard.add(event.getTwo());
            for (WindowPatternCard c : possiblePatternCard) {
                if (c.getFrontPattern() == pattern) {
                        gameController.choosePattern(nick, c, false);
                    break;
                }
                if (c.getRearPattern() == pattern) {
                    gameController.choosePattern(nick, c, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to display private objective
     */
    private void displayObjective(){
        Image img = null;
        try {
            img = new Image(privObj.getImgPath());
        } catch (Exception e) {
            img = new Image("objectives/private/30%/objectives-12.png");
        }
        imgPrivateObjective.setImage(img);
        imgPrivateObjective.setFitHeight(Settings.instance().getCARD_HEIGHT());
        imgPrivateObjective.setPreserveRatio(true);
        imgPrivateObjective.setSmooth(true);
        imgPrivateObjective.setCache(true);
    }

    /**
     * Given a VBox it creates a GridPane representing a WindowPattern,
     * then it add a button to choose that pattern card  and a number representing the value
     * @param vBox
     * @param pattern
     */
    private void populate(VBox vBox, WindowPattern pattern){
        // Create Grid for matrix of this pattern
        GridPane gPane = new GridPane();
        gPane.setPadding(new Insets(25, 25, 25, 25));
        gPane.setVgap(10);
        gPane.setHgap(10);
        gPane.setAlignment(Pos.CENTER);
        // Create "choose this patten" button to invoke controller
        Button btnChooseThis = new Button();
        btnChooseThis.setText("Choose this");
        btnChooseThis.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                chooseThis(pattern);
                stage.close();
            }
        });
        // Create Text to show difficulty
        Text txtFavour = new Text();
        txtFavour.setText("Favour: "+pattern.getDifficulty());
        // Add new element to box
        vBox.getChildren().add(gPane);
        vBox.getChildren().add(txtFavour);
        vBox.getChildren().add(btnChooseThis);
        for (int row = 0; row < Settings.instance().getMATRIX_NR_ROW(); row++) {
            for (int col = 0; col < Settings.instance().getMATRIX_NR_ROW(); col++) {
                Button currentCell = new Button();
                Constraint constraint = pattern.getConstraintsMatrix()[row][col];
                if (constraint != null && constraint.getImgPath() != "") {
                    Image image = new Image(constraint.getImgPath());
                    ImageView bg = new ImageView(image);
                    bg.setFitHeight(Settings.instance().getCELL_DIMENSION());
                    bg.setFitWidth(Settings.instance().getCELL_DIMENSION());
                    bg.setPreserveRatio(true);
                    bg.setSmooth(true);
                    bg.setCache(true);
                    currentCell.setGraphic(bg);
                } else {
                    ImageView bg = new ImageView();
                    bg.setFitHeight(Settings.instance().getCELL_DIMENSION());
                    bg.setFitWidth(Settings.instance().getCELL_DIMENSION());
                    currentCell.setGraphic(bg);
                }
                if (constraint != null)
                    currentCell.setStyle("-fx-background-color:" + Settings.instance().getMapBgColour().get(constraint.getColour()));
                gPane.add(currentCell, col, row);
            }
        }
    }


}
