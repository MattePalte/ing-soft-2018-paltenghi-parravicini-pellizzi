package project.ing.soft.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.ing.soft.Settings;
import project.ing.soft.controller.IController;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;
import project.ing.soft.model.gamemodel.events.PatternCardDistributedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChoosePatternController {

    private IController gameController;
    private String nick;
    private Stage stage;
    private PatternCardDistributedEvent event;
    private PrivateObjective privObj;
    private  final Logger log = Logger.getLogger(Objects.toString(this));

    @FXML private Text txtTitle;
    @FXML private VBox frontFirst;
    @FXML private VBox rearFirst;
    @FXML private VBox frontSecond;
    @FXML private VBox rearSecond;
    @FXML private VBox vMyObjective;
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
        txtTitle.setText(nick + " choose a pattern");
        populate(frontFirst, event.getOne().getFrontPattern());
        populate(rearFirst, event.getOne().getRearPattern());
        populate(frontSecond, event.getTwo().getFrontPattern());
        populate(rearSecond, event.getTwo().getRearPattern());
        displayObjective();
    }

    /**
     * Get pattern card and frontSide from pattern
     * and then send our chosen card to the game controller
     * @param pattern
     */
    private void chooseThis(WindowPattern pattern) {
        try {
            List<WindowPatternCard> possiblePatternCard = new ArrayList<>();
            possiblePatternCard.add(event.getOne());
            possiblePatternCard.add(event.getTwo());
            for (WindowPatternCard c : possiblePatternCard) {
                if (c.getFrontPattern() == pattern || c.getRearPattern() == pattern) {
                    gameController.choosePattern(nick, c, c.getRearPattern() == pattern);
                    break;
                }
            }
        } catch (Exception e) {
            String stack = Arrays.toString(e.getStackTrace());
            log.log(Level.INFO, stack);
        }
    }

    /**
     * Method to display private objective
     */
    private void displayObjective(){
        // Set style
        StyleBooster.forObjectiveCard(vMyObjective, 10);
        Image img = null;
        try {
            img = new Image(privObj.getImgPath());
        } catch (Exception e) {
            img = new Image("objectives/private/30%/objectives-12.png");
        }
        imgPrivateObjective.setImage(img);
        imgPrivateObjective.setFitHeight(Settings.instance().getCardHeight());
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
        // Set style
        StyleBooster.forPatternCard(vBox, 10);
        // Create "choose this patten" button to invoke controller
        Button btnChooseThis = new Button();
        btnChooseThis.setText("Choose this");
        btnChooseThis.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                chooseThis(pattern);
                disableOtherButtons();
            }
        });
        // Create Text to show difficulty
        Text txtFavour = new Text();
        txtFavour.setText("Favour: "+pattern.getDifficulty());
        // Create GridPane for pattern Card
        GridPane gridPattern = ElementCreator.createPattern(pattern, Settings.instance().getCellDimension());
        // Add new element to box
        vBox.getChildren().add(gridPattern);
        vBox.getChildren().add(txtFavour);
        vBox.getChildren().add(btnChooseThis);
    }

    private void disableOtherButtons(){
        frontFirst.setDisable(true);
        rearFirst.setDisable(true);
        frontSecond.setDisable(true);
        rearSecond.setDisable(true);
    }


}
