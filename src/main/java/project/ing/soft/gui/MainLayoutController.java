package project.ing.soft.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.ing.soft.Settings;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemodel.events.*;
import project.ing.soft.model.gamemodel.IGameModel;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainLayoutController extends UnicastRemoteObject implements IEventHandler, IToolCardParametersAcquirer,  Serializable{
    private IGameModel localCopyOfTheStatus;
    private Player myPlayer;
    private String ownerNameOfTheView;
    private transient IController gameController;
    private transient String token;
    private Stage primaryStage;
    private final transient Logger log = Logger.getLogger(Objects.toString(this));

    //region Constants
    // default configuration for sockets
    private static final String FOCUS_BG_COLOR = "#19e4ff";
    private static final String WHITE = "#fff";
    private static final String FX_BACKGROUND = "-fx-background-color:";
    private double screenWidth;
    private double cellDimension;
    private double smallCellDimension;

    private static final String ID_DRAFTPOOL = "draftPool";
    private static final String ID_MATRIX = "matrixBox";
    private static final String ID_ROUNDTRACKER = "roundtracker";
    private static final String ID_TOOLCARDBOX = "toolcardBox";
    private static final String ID_BOX_VALUE = "valueBox";
    private static final String ID_BOX_ACTION = "main_actions";
    private static final String[] AREA_IDS = {ID_DRAFTPOOL, ID_ROUNDTRACKER, ID_MATRIX, ID_TOOLCARDBOX, ID_BOX_ACTION};
    //endregion

    //region Retrieving parameters methods
    private transient ExecutorService turnExecutor;
    private final transient  List<Object> parameters = new ArrayList<>();
    private transient Future waitingForParameters;

    private void put(Object obj) {
        synchronized (parameters) {
            parameters.add(obj);
            parameters.notifyAll();
        }
    }

    private Object getObj() throws InterruptedException {
        synchronized (parameters){
            while (parameters.isEmpty()) parameters.wait();
            Object objRemoved = parameters.remove(0);
            parameters.notifyAll();
            return objRemoved;
        }
    }
    /**
     * Method to wait until the player select a Coordinate. It means that a Coordinate
     * object has been added to the waiting queue (list of parameters).
     * @return a Coordinate
     * @throws InterruptedException if the player interrupt the movement
     */
    private Coordinate getCoord() throws InterruptedException {
        Object obj = null;
        do {
            obj = getObj();
        } while (! (obj instanceof Coordinate));
        return (Coordinate) obj;
    }

    /**
     * Method to wait until the player select a Die. It means that a Die object
     * has been added to the waiting queue (list of parameters).
     * @return a Die
     * @throws InterruptedException if the player interrupt the movement
     */
    public Die getDie() throws InterruptedException {
        Object obj = null;
        do {
            obj = getObj();
        } while (! (obj instanceof Die));
        return (Die) obj;
    }

    /**
     * Method to wait until the player select a Value. It means that an integer
     * has been added to the waiting queue (list of parameters).
     * @return a Value
     * @throws InterruptedException if the player interrupt the movement
     */
    public Integer getValue() throws InterruptedException {
        Object obj = null;
        do {
            obj = getObj();
        } while (! (obj instanceof Integer));
        return (Integer) obj;
    }

    public String getString() throws InterruptedException {
        Object obj = null;
        do {
            obj = getObj();
        } while (! (obj instanceof String));
        return (String) obj;
    }

    /**
     * Method to add a Coordinate to the waiting queue (list of parameters).
     * @param coord Coordinate selected by the user
     */
    private void collectCoordinate(Coordinate coord){
        put(coord);
    }
    /**
     * Method to add a Die to the waiting queue (list of parameters).
     * @param die Die selected by the user
     */
    private void collectDie(Die die){
        put(die);
    }
    /**
     * Method to add an Integer to the waiting queue (list of parameters).
     * @param value Coordinate selected by the user
     */
    private void collectValue(Integer value){
        put(value);
    }

    private void collectString(String strChosen){
        put(strChosen);
    }
    /**
     * Method to add an Integer representing a toolcard index to the waiting queue
     * (list of parameters).
     * @param index Index selected by the user
     */
    private void collectToolcardIndex(Integer index) {
        put(index);
    }


    //endregion

    @FXML
    protected void initialize(){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        if (Settings.instance().getMinScreenSize() > primaryScreenBounds.getWidth()) {
            screenWidth = primaryScreenBounds.getWidth();
        } else {
            screenWidth = Settings.instance().getMinScreenSize();
        }
        cellDimension = screenWidth /30;
        smallCellDimension = cellDimension /1.5;

        mainScrollPane.setMaxHeight(primaryScreenBounds.getHeight());
        mainScrollPane.setMinHeight(primaryScreenBounds.getWidth());
        roundtrackerScrollPane.setMinHeight(smallCellDimension + 30);
        roundtrackerScrollPane.setMaxHeight(smallCellDimension + 30);
        roundtrackerScrollPane.setFitToWidth(true);
        roundtrackerScrollPane.setFitToHeight(true);
        StyleBooster.forInstructionBox(instructionBox, 5);
        turnExecutor = Executors.newSingleThreadExecutor();
    }

    //region Getter e Setter

    /**
     * Saves the stage as an internal reference to modify its Scene or dimensions
     * if needed.
     * @param primaryStage main window of the game
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Saves the token for reconnection
     * @param token for reconnection
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Retrieve the main window of the game
     * @return main window stage of the game.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Saves the nickname to use during each method call to the controller
     * @param ownerNameOfTheView nickname of the current player
     */
    public void setOwnerNameOfTheView(String ownerNameOfTheView) {
        this.ownerNameOfTheView = ownerNameOfTheView;
    }
    public void setLocalCopyOfTheStatus(IGameModel localCopyOfTheStatus) {
        this.localCopyOfTheStatus = localCopyOfTheStatus;
        for (Player p : localCopyOfTheStatus.getPlayerList()){
            if (p.getName().equals(ownerNameOfTheView)) {
                this.myPlayer = p;
                break;
            }
        }
    }

    /**
     * Get the nick name of owner of this running client
     * @return nickname of the owner of the client
     */
    public String getOwnerNameOfTheView() {
        return ownerNameOfTheView;
    }

    //endregion

    //region GUI Elements
    @FXML private TextField favourField;
    @FXML private TextField tokenField;

    // TO DESPLAY INFO
    @FXML private Text status;
    @FXML private HBox content;
    @FXML private Text roundnumber;
    @FXML private HBox turnlist;
    @FXML private Text instructionTxt;
    @FXML private Pane instructionBox;

    // PHASE 2 - ACTIONS
    @FXML private Button btnPlaceDie;
    @FXML private Button btnPlayToolCard;
    @FXML private Button btnEndTurn;
    @FXML private Button btnCancel;
    //endregion

    @FXML private ProgressBar timeout;
    @FXML private GridPane roundtracker;
    @FXML private ScrollPane roundtrackerScrollPane;
    @FXML private ScrollPane mainScrollPane;
    @FXML private Pane imgPrivateObjective;

    public MainLayoutController() throws RemoteException {
        super();
    }

    //region Event Responding
    @Override
    public void respondTo(ToolcardActionRequestEvent event) {
        // PRE-SETUP
        endingOperation();
        btnCancel.setDisable(false);
        IToolCardParametersAcquirer myAcquirer = this;
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        waitingForParameters = turnExecutor.submit(
                () -> {
                        try {

                            ToolCard chosenToolCard = event.getCard();
                            chosenToolCard.fill(myAcquirer);
                            gameController.playToolCard(ownerNameOfTheView, chosenToolCard);
                        } catch (InterruptedException e) {
                            displayError(e);
                            log.log(Level.INFO,"Interrupted play toolcard of " + ownerNameOfTheView);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            displayError(e);
                            btnPlayToolCard.setDisable(false);
                        }
                    }
        );
    }

    @Override
    public void respondTo(SetTokenEvent event) {
        this.token = event.getToken();
    }

    @Override
    public void respondTo(PlayerReconnectedEvent event) {
        log.log(Level.INFO, event.getNickname() + ": reconnected");
        drawRoundTracker();
    }

    @Override
    public void respondTo(PlayerDisconnectedEvent event) {
        log.log(Level.INFO, event.getNickname() + ": disconnected");
        drawRoundTracker();
    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        // not used event
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        displayPrivateObjective();
        displayPublicCard(getPrimaryStage().getScene());
        synchronized (this) {
            // show connection info
            tokenField.setText(token);
        }
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        // nothing to do, because it is handle primarly by the RealView itself
    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        // implemented in real view
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        Timeline timeLine = new Timeline();
        timeLine.setCycleCount(1);
        Timestamp endTurn = event.getEndTurnTimeStamp();
        if(endTurn != null) {
            long initialValue = endTurn.getTime() - System.currentTimeMillis();
            DoubleProperty time = new SimpleDoubleProperty(initialValue);
            timeout.setStyle("-fx-accent: " + FOCUS_BG_COLOR +";");
            timeout.progressProperty().bind(time.divide(Settings.instance().getTurnTimeout()));

            timeLine.getKeyFrames().add(new KeyFrame(Duration.millis(initialValue), new KeyValue(time, 0)));
            timeLine.play();
        }
        enableActions();
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        // new model
        localCopyOfTheStatus = event.getaGameCopy();
        for (Player p : localCopyOfTheStatus.getPlayerList()){
            if (p.getName().equals(ownerNameOfTheView)) {
                this.myPlayer = p;
                break;
            }
        }
        // Draw other things
        log.log(Level.INFO,getClass().getName() + " is handling model changed event");
        drawMySituation();
        displayOtherPlayerSituation(primaryStage.getScene());
        drawDraftPool();
        displayToolCard(getPrimaryStage().getScene());
        drawRoundTracker();
        if(!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView))
            disableAll();
        if (localCopyOfTheStatus.getFavours().get(ownerNameOfTheView) != null) {
            int favoursLeft = localCopyOfTheStatus.getFavours().get(ownerNameOfTheView);
            String oneFavour = " * ";
            favourField.setText(
                    new String(new char[favoursLeft]).replace("\0", oneFavour));
            favourField.setStyle("-fx-font-size: 20px;");
        }
    }

    @Override
    public void respondTo(MyTurnEndedEvent event) {
        endingOperation();
        log.log(Level.INFO,"Timeeeer event happened");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Timer expired");
        String s ="I'm sorry, you have finished your time...";
        alert.setHeaderText(s);
        alert.show();
        synchronized (this) {
            btnCancel.setDisable(true);
            timeout.progressProperty().bind(new SimpleDoubleProperty(0));
            instructionTxt.setText("");
        }
    }
    //endregion

    /**
     * Method to interrupt al running operation and let the user perform a new one
     */
    private void endingOperation() {
        disableAll();
        synchronized (this) {
            btnPlaceDie.setDisable(true);
            btnPlayToolCard.setDisable(true);
            //btnEndTurn.setDisable(true);
        }
        if(waitingForParameters != null && !waitingForParameters.isDone())
            waitingForParameters.cancel(true);
    }

    //region Draw Things

    /**
     * Method to represent current player situation
     */
    private synchronized void drawMySituation() {
        // get stuff of the owner of the view
        WindowPattern wndPtrn = myPlayer.getPattern();
        Die[][] placedDie = myPlayer.getPlacedDice();
        // if no pattern setted yet -> draw nothing
        if (wndPtrn == null) return;
        Scene scene = getPrimaryStage().getScene();
        // print die and constraint
        Pane matrixContainer = (Pane) scene.lookup("#" + ID_MATRIX);
        GridPane matrixPane = ElementCreator.createClickablePattern(wndPtrn, placedDie, (int) cellDimension, "pos");
        matrixContainer.getChildren().clear();
        matrixContainer.getChildren().add(0, matrixPane);
        initializeMatrixButtons(matrixPane, "pos");
    }

    /**
     * Method to represent the draftpool
     */
    private synchronized void drawDraftPool() {
        Scene scene = getPrimaryStage().getScene();
        GridPane paneDraft = (GridPane) scene.lookup("#" + ID_DRAFTPOOL);
        List<Die> draft = localCopyOfTheStatus.getDraftPool();
        paneDraft.getChildren().clear();
        for (int pos = 0 ; pos < draft.size(); pos++) {
            Die currentDie = draft.get(pos);
            // Create Image
            Image image = new Image(currentDie.getImgPath());
            ImageView bg = new ImageView(image);
            bg.setFitHeight(smallCellDimension);
            bg.setFitWidth(smallCellDimension);
            bg.setPreserveRatio(true);
            bg.setSmooth(true);
            bg.setCache(true);
            // Create Button
            Button currentCell = new Button();
            paneDraft.add(currentCell, pos/5,pos%5);
            currentCell.setGraphic(bg);
            currentCell.setStyle(FX_BACKGROUND + WHITE);
            currentCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    disableAll();
                    collectDie(new Die(currentDie));
                }
            });
        }
    }

    /**
     * Method to draw possible integer values. It is used on a newly created scene with
     * specific buttons, one for each possible value.
     * @param scene that contains the buttons
     * @param idPane name of the container of the button
     * @param message message to guide the user
     * @param values possible value numbers
     */
    private synchronized void drawValues(Scene scene, String idPane, String message, Integer... values) {
        GridPane pane = (GridPane) scene.lookup("#" + idPane);
        Text lblMessage = (Text) scene.lookup("#lblMessage");
        lblMessage.setText(message);
        pane.getChildren().clear();
        for (int pos = 0 ; pos < values.length; pos++) {
            int currentValue = values[pos];
            // Create Button
            Button currentCell = new Button();
            currentCell.setText(String.valueOf(pos));
            pane.add(currentCell, pos,0);
            currentCell.setStyle(FX_BACKGROUND + WHITE);
            currentCell.setText(Integer.toString(currentValue));
            currentCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    disableAll();
                    Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    currentStage.close();
                    collectValue(currentValue);
                }
            });
        }
    }

    private synchronized void drawStrings(Scene scene, String idPane, String message, String... values) {
        GridPane pane = (GridPane) scene.lookup("#" + idPane);
        Text lblMessage = (Text) scene.lookup("#lblMessage");
        lblMessage.setText(message);
        pane.getChildren().clear();
        for (int pos = 0 ; pos < values.length; pos++) {
            String currentString = values[pos];
            // Create Button
            Button currentCell = new Button();
            currentCell.setText(String.valueOf(pos));
            pane.add(currentCell, pos,0);
            currentCell.setStyle(FX_BACKGROUND + WHITE);
            currentCell.setText(currentString);
            currentCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    disableAll();
                    Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    currentStage.close();
                    collectString(currentString);
                }
            });
        }
    }

    /**
     * Method to represent RoundTracker
     */
    private synchronized void drawRoundTracker() {
        RoundTracker roundTracker = localCopyOfTheStatus.getRoundTracker();
        // Draw round number and turn list
        roundnumber.setText("Round nr: " + roundTracker.getCurrentRound());
        turnlist.getChildren().clear();
        for(Player p : localCopyOfTheStatus.getCurrentTurnList()){
            TextField text = new TextField(p.getName());
            text.setEditable(false);
            text.setAlignment(Pos.CENTER);
            if(p.isConnected())
                text.setStyle("-fx-text-inner-color:" + Colour.GREEN.getWebColor());
            else
                text.setStyle("-fx-text-inner-color:" + Colour.RED.getWebColor());
            turnlist.getChildren().add(text);
            turnlist.getChildren().add(new Text(">"));
        }
        if(roundTracker.getCurrentRound() < Settings.instance().getNrOfRound())
            turnlist.getChildren().add(new Text("Next Round"));
        else
            turnlist.getChildren().add(new Text("End of the game"));
        // Draw content of the roundtracker
        Scene scene = getPrimaryStage().getScene();
        GridPane paneRoundTracker = (GridPane) scene.lookup("#" + ID_ROUNDTRACKER);
        List<Die> diceLeft = roundTracker.getDiceLeftFromRound();
        paneRoundTracker.getChildren().clear();
        for (int pos = 0 ; pos < diceLeft.size(); pos++) {
            int row = pos/9;
            int col = pos%9;
            Die currentDie = diceLeft.get(pos);
            // Create Image
            Image image = new Image(currentDie.getImgPath());
            ImageView bg = new ImageView(image);
            bg.setFitHeight(smallCellDimension);
            bg.setFitWidth(smallCellDimension);
            bg.setPreserveRatio(true);
            bg.setSmooth(true);
            bg.setCache(true);
            // Create Button
            Button currentCell = new Button();
            paneRoundTracker.add(currentCell, col, row);
            currentCell.setGraphic(bg);
            currentCell.setStyle(FX_BACKGROUND + WHITE);
            currentCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    disableAll();
                    collectDie(new Die(currentDie));
                }
            });
        }
    }
    private synchronized void displayPrivateObjective() {
        ImageView ivPrivateObj = ElementCreator.createCard(myPlayer.getPrivateObjective(), getPrimaryStage());
        imgPrivateObjective.getChildren().add(0, ivPrivateObj);
        imgPrivateObjective.setMaxHeight(screenWidth /6);
        StyleBooster.forObjectiveCard(imgPrivateObjective, 5);
    }

    /**
     * Method to represent Toolcard
     * @param scene where toolcard FXML eleemnts can be found
     */
    private synchronized void displayToolCard(Scene scene) {
        List<ToolCard> tCard = localCopyOfTheStatus.getToolCards();
        for (int index = 0; index<3; index++) {
            ToolCard currentTool = tCard.get(index);
            ImageView iv = ElementCreator.createCard(currentTool, getPrimaryStage());
            StackPane pane = (StackPane) scene.lookup("#toolcard" + index);
            pane.getChildren().clear();
            int cost = localCopyOfTheStatus.getToolCardCost().get(tCard.get(index).getTitle());
            Text lblCost = new Text("Favours required -> " + String.valueOf(cost));
            lblCost.setFill(Color.rgb(43, 161, 255));
            pane.getChildren().add(0, iv);
            pane.getChildren().add(1, lblCost);
            pane.setAlignment(lblCost, Pos.BOTTOM_LEFT);
            StyleBooster.forToolCardCard(pane, 7);
            int finalIndex = index;
            iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    disableAll();
                    collectToolcardIndex(finalIndex);
                }
            });
        }
    }

    /**
     * Method to display the situation of other players
     * @param scene where the container exists
     */
    private synchronized void displayOtherPlayerSituation(Scene scene) {
        if (localCopyOfTheStatus == null) return;
        List<Player> listOfPlayer = localCopyOfTheStatus.getPlayerList();
        if (listOfPlayer == null) return;
        Pane area = (Pane) scene.lookup("#otherPlayersArea");
        for (int index = 0; index < listOfPlayer.size(); index++){
            Player p = listOfPlayer.get(index);
            // Create Text to show difficulty
            Text txtPlayerName = new Text(p.getName());
            // Create and style box
            VBox playerBox = new VBox();
            playerBox.setAlignment(Pos.CENTER);
            StyleBooster.forPatternCard(playerBox, 10);
            // Create GridPane for pattern Card
            GridPane playerPattern = ElementCreator.createPattern(p.getPattern(), p.getPlacedDice(), Settings.instance().getCellDimension());
            // Add elements
            playerBox.getChildren().add(0,txtPlayerName);
            playerBox.getChildren().add(0,playerPattern);
            // Add player to area of other player
            if (area.getChildren().size() > index) {
                area.getChildren().set(index, playerBox);
            } else {
                area.getChildren().add(index, playerBox);
            }
        }

    }

    /**
     * Method to represent public cards
     * @param scene where public card FXML eleemnts can be found
     */
    private synchronized void displayPublicCard(Scene scene) {
        List<PublicObjective> pubCard = localCopyOfTheStatus.getPublicObjective();
        for (int index = 0; index<3; index++) {
            PublicObjective currentPub = pubCard.get(index);
            ImageView iv = ElementCreator.createCard(currentPub, getPrimaryStage());
            Pane pane = (Pane) scene.lookup("#publicCard" + index);
            pane.getChildren().add(0, iv);
            StyleBooster.forObjectiveCard(pane, 7);
        }
    }
    //endregion

    //region IView interface

    //endregion

    //region Fixed Button Handling

    /**
     * Listener attached through FXML to the starting of an action.
     * The game action is: placement of a die on the pattern window.
     * @throws Exception if something goes wrong
     */
    public void btnPlaceDieOnCLick() {
        // PRE-SETUP
        endingOperation();
        synchronized (this) {
            btnCancel.setDisable(false);
        }
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        waitingForParameters = turnExecutor.submit(
                () -> {
                        try {
                            Coordinate chosenCoord = getCoordinate("Choose a position to place the Die");
                            Die chosenDie = getDieFromDraft("Chose a die to place");
                            disableAll();
                            gameController.placeDie(getOwnerNameOfTheView(), chosenDie, chosenCoord.getRow(), chosenCoord.getCol());
                        } catch (InterruptedException e) {
                            log.log(Level.INFO,"Interrupted place die of " + ownerNameOfTheView);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            displayError(e);
                        } finally {
                            enableActions();
                        }
                    }
        );
    }
    /**
     * Listener attached through FXML to the starting of an action.
     * The game action is: select a toolcard and use it.
     * @throws Exception if something goes wrong
     */
    public void btnPlayToolCardOnCLick() {
        // PRE-SETUP
        endingOperation();
        synchronized (this) {
            btnCancel.setDisable(false);
        }
        IToolCardParametersAcquirer myAcquirer = this;
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        waitingForParameters = turnExecutor.submit(
                () -> {
                        try {
                            showPickToolCardIndex("Choose a toolcard to use");
                            Integer chosenIndex = getValue();
                            ToolCard chosenToolCard = localCopyOfTheStatus.getToolCards().get(chosenIndex);
                            chosenToolCard.fill(myAcquirer);
                            gameController.playToolCard(ownerNameOfTheView, chosenToolCard);
                        } catch (InterruptedException e) {
                            log.log(Level.INFO,"Interrupted play toolcard of " + ownerNameOfTheView);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            displayError(e);
                        } finally {
                            enableActions();
                        }
                    }
        );
    }
    /**
     * Listener attached through FXML to the starting of an action.
     * The game action is: cancel current action and let the user select a new one.
     * @throws Exception if something goes wrong
     */
    public void btnCancelOnCLick() {
        endingOperation();
        enableActions();
        synchronized (this) {
            instructionTxt.setText("");
        }
    }
    /**
     * Listener attached through FXML to the starting of an action.
     * The game action is: say that you have finished your turn.
     * @throws Exception if something goes wrong
     */
    public void btnEndTurnOnCLick() {
        endingOperation();
        synchronized (this) {
            btnCancel.setDisable(true);
            timeout.progressProperty().bind(new SimpleDoubleProperty(0));
            instructionTxt.setText("");
        }
        try {
            gameController.endTurn(ownerNameOfTheView);
        } catch (Exception e) {
            log.log(Level.INFO, getClass().getName() + " -> Cause: "+e.getCause() + "\n Message " + e.getMessage());
            log.log(Level.INFO, Arrays.toString(e.getStackTrace()));
        }
    }
    //endregion

    /**
     * Method to initialize the button of the window pattern of the current client.
     * Each button will add to the waiting queue (list of parameters) a Coordinate
     * representing its position in the window pattern.
     * @param matrixPane container of the cells
     * @param prefixTag tag used as a prefix of current window pattern's cells
     */
    private synchronized void initializeMatrixButtons(GridPane matrixPane, String prefixTag){
        StyleBooster.forPatternCard(matrixPane, 15);
        // set button of pattern
        for (int row = 0; row < Settings.instance().getMatrixNrRow(); row++) {
            for (int col = 0; col < Settings.instance().getMatrixNrCol(); col++) {
                Button currentCell = (Button) matrixPane.lookup("#" + prefixTag + row + col);
                int finalRow = row;
                int finalCol = col;
                currentCell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        disableAll();
                        collectCoordinate(new Coordinate(finalRow, finalCol));
                    }
                });
            }
        }
    }

    //region Focus
    private synchronized void focus(String idPane, boolean isFocused){
        Scene scene = getPrimaryStage().getScene();
        Pane pane = (Pane) scene.lookup("#" + idPane);
        if (isFocused) {
            pane.setStyle(FX_BACKGROUND + FOCUS_BG_COLOR);
            for (Node n : pane.getChildren()) {
                n.setDisable(false);
            }
        } else {
            pane.setStyle("");
            for (Node n : pane.getChildren()) {
                n.setDisable(true);
            }
        }
    }

    private synchronized void focusOn(String idPane) {
        focus(idPane, true);
    }

    private synchronized void focusOn(String idPane, String message){
        focusOn(idPane);
        instructionTxt.setText(message);
    }

    private synchronized void focusOff(String idPane) {
        focus(idPane, false);
    }

    private synchronized void disableAll(){
        for (String id : AREA_IDS) {
            focusOff(id);
        }
    }

    private synchronized void enableOnly(String idPane, String message) {
        for (String id : AREA_IDS) {
            if (id.equals(idPane)) {
                focusOn(id, message);
            } else {
                focusOff(id);
            }
        }
    }

    private synchronized void enableActions(){
        btnPlaceDie.setDisable(false);
        btnEndTurn.setDisable(false);
        btnPlayToolCard.setDisable(false);
        btnCancel.setDisable(true);
        focusOn(ID_BOX_ACTION, "Select an action ");
    }
    //endregion


    //region Possile View Interface

    private synchronized void showPickValues(String message, Integer... values) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/layout/chose_value.fxml"));
                Parent root1 = null;
                try {
                    root1 = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    log.log(Level.INFO, Arrays.toString(e.getStackTrace()));
                    return;
                }
                fxmlLoader.setController(this);
                Stage stage = new Stage();
                Scene pickValueScene = new Scene(root1);
                stage.setScene(pickValueScene);
                stage.show();
                drawValues(pickValueScene, ID_BOX_VALUE, message, values);
            }
        });
    }
    private synchronized void showPickString(String message, String... values) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/layout/chose_value.fxml"));
                Parent root1 = null;
                try {
                    root1 = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    log.log(Level.INFO, Arrays.toString(e.getStackTrace()));
                    return;
                }
                fxmlLoader.setController(this);
                Stage stage = new Stage();
                Scene pickValueScene = new Scene(root1);
                stage.setScene(pickValueScene);
                stage.show();
                drawStrings(pickValueScene, ID_BOX_VALUE, message, values);
            }
        });
    }
    private synchronized void showPickToolCardIndex(String message) {
        enableOnly(ID_TOOLCARDBOX, message);
    }
    //endregion

    //region parameter acquirer

    /**
     * Method to wait until the player select a Die of the draftpool.
     * It means that a Die object has been added to the
     * waiting queue (list of parameters).
     * @return a Die
     * @throws InterruptedException if the player interrupt the movement
     */
    @Override
    public Die getDieFromDraft(String message) throws InterruptedException {
        enableOnly(ID_DRAFTPOOL,message);
        return getDie();
    }
    /**
     * Method to wait until the player select a Die of the RoundTracker.
     * It means that a Die object has been added to the
     * waiting queue (list of parameters).
     * @return a Die
     * @throws InterruptedException if the player interrupt the movement
     */
    @Override
    public Die getDieFromRound(String message) throws InterruptedException {
        enableOnly(ID_ROUNDTRACKER,message);
        return getDie();
    }
    /**
     * Method to wait until the player select a Coordinate on his/her window pattern.
     * It means that a Coordinate object has been added to the
     * waiting queue (list of parameters).
     * @return a Coordinate
     * @throws InterruptedException if the player interrupt the movement
     */
    @Override
    public Coordinate getCoordinate(String message) throws InterruptedException {
        enableOnly(ID_MATRIX,message);
        return getCoord();
    }
    /**
     * Method to wait until the player select a value from the given list.
     * It means that an Integer has been added to the
     * waiting queue (list of parameters).
     * @return an int
     * @throws InterruptedException if the player interrupt the movement
     */
    @Override
    public int getValue(String message, Integer... values) throws InterruptedException {
        showPickValues(message, values);
        return getValue();
    }

    @Override
    public boolean getAnswer(String message) throws InterruptedException {
        showPickString(message, "yes", "no");
        return getString() == "yes";
    }


    //endregion

    /**
     * Saves the controller of the MVC as a reference to ask for actions to the server.
     * @param gameController controller of the game
     */
    public void setGameController(IController gameController) {
        this.gameController = gameController;
    }

    /**
     * It is used to display msgbox to the user in case o exception server side
     * or client side
     * @param ex Exception occurred
     */
    private void displayError(Exception ex){
        String stack = Arrays.toString(ex.getStackTrace());
        log.log(Level.INFO, stack);
        Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exeption");
                alert.setHeaderText("Information Alert");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        );
    }

}


