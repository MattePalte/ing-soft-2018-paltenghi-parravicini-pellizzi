package project.ing.soft.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
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
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.ing.soft.Settings;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemodel.events.*;
import project.ing.soft.model.gamemodel.IGameModel;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainLayoutController extends UnicastRemoteObject implements IEventHandler, IToolCardParametersAcquirer,  Serializable{
    private IGameModel localCopyOfTheStatus;
    private Player myPlayer;
    private String ownerNameOfTheView;
    private transient IController gameController;
    private transient String token;
    private transient PrintStream out;
    private Stage primaryStage;

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

    private Coordinate getCoord() throws InterruptedException {
        Object obj = null;
        do {
            obj = getObj();
        } while (! (obj instanceof Coordinate));
        return (Coordinate) obj;
    }
    public Die getDie() throws InterruptedException {
        Object obj = null;
        do {
            obj = getObj();
        } while (! (obj instanceof Die));
        return (Die) obj;
    }
    public Integer getValue() throws InterruptedException {
        Object obj = null;
        do {
            obj = getObj();
        } while (! (obj instanceof Integer));
        return (Integer) obj;
    }

    private void collectCoordinate(Coordinate coord){
        put(coord);
    }

    private void collectDie(Die die){
        put(die);
    }

    private void collectValue(Integer value){
        put(value);
    }

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
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public void setOut(PrintStream out) {
        this.out = out;
    }
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
    @FXML private Text turnlist;
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
                            out.println("Interrupted play toolcard of " + ownerNameOfTheView);
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
    public void respondTo(CurrentPlayerChangedEvent event) {
        // not used event
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        displayPrivateObjective();
        displayToolCard(getPrimaryStage().getScene());
        displayPublicCard(getPrimaryStage().getScene());
        synchronized (this) {
            // show connection info
            tokenField.setText(token);
        }
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        out.println("Game finished!");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Final Rank:");
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> pointsDescriptor = event.getPointsDescriptor();
        for(Player p : localCopyOfTheStatus.getPlayerList()){
            stringBuilder.append(pointsDescriptor.get(p.getName()));
        }
        for (Pair<Player, Integer> aPair : event.getRank()){
            String playerLine = aPair.getKey().getName() + " => " + aPair.getValue() + "\n";
            stringBuilder.append(playerLine);
            out.println(aPair.getKey() + " => " + aPair.getValue());
        }
        alert.setHeaderText(stringBuilder.toString());
        alert.show();
        out.println(stringBuilder.toString());
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
            timeout.progressProperty().bind(time.divide(Settings.instance().getTURN_TIMEOUT()));

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
        out.println("gestione di model changed");
        drawMySituation();
        displayOtherPlayerSituation(primaryStage.getScene());
        drawDraftPool();
        drawRoundTracker();
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
        out.println("Timeeeer");
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

    private void endingOperation() {
        disableAll();
        synchronized (this) {
            btnPlaceDie.setDisable(true);
            btnPlayToolCard.setDisable(true);
            btnEndTurn.setDisable(true);
        }
        if(waitingForParameters != null && !waitingForParameters.isDone())
            waitingForParameters.cancel(true);
    }

    //region Draw Things

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
    private synchronized void drawRoundTracker() {
        RoundTracker roundTracker = localCopyOfTheStatus.getRoundTracker();
        // Draw round number and turn list
        roundnumber.setText("Round nr: " + roundTracker.getCurrentRound());
        StringBuilder listOfTurn = new StringBuilder();
        for (Player p : localCopyOfTheStatus.getCurrentTurnList()) {
            listOfTurn.append(p.getName());
            listOfTurn.append(" > ");
        }
        if (roundTracker.getCurrentRound() < Settings.instance().getNrOfRound()) {
            listOfTurn.append("round ");
            listOfTurn.append(roundTracker.getCurrentRound() + 1);
        } else {
            listOfTurn.append("end ");
        }
        turnlist.setText(listOfTurn.toString());
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
    private synchronized void displayToolCard(Scene scene) {
        List<ToolCard> tCard = localCopyOfTheStatus.getToolCards();
        for (int index = 0; index<3; index++) {
            ToolCard currentTool = tCard.get(index);
            ImageView iv = ElementCreator.createCard(currentTool, getPrimaryStage());
            Pane pane = (Pane) scene.lookup("#toolcard" + index);
            pane.getChildren().add(0, iv);
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
            GridPane playerPattern = ElementCreator.createPattern(p.getPattern(), p.getPlacedDice(), Settings.instance().getCELL_DIMENSION());
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

    private String getTime() {
        Calendar c = Calendar.getInstance(); //automatically set to current time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(c.getTime());
    }
    //endregion

    //region Fixed Button Handling

    public void btnPlaceDieOnCLick() throws Exception {
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
                            out.println( getTime() + " - " + "Interrupted place die of " + ownerNameOfTheView);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            displayError(e);
                        } finally {
                            enableActions();
                        }
                    }
        );
    }

    public void btnPlayToolCardOnCLick() throws Exception {
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
                            out.println("Interrupted play toolcard of " + ownerNameOfTheView);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            displayError(e);
                        } finally {
                            enableActions();
                        }
                    }
        );
    }

    public void btnCancelOnCLick() throws Exception{
        endingOperation();
        enableActions();
        synchronized (this) {
            instructionTxt.setText("");
        }
    }
    public void btnEndTurnOnCLick() throws Exception {
        endingOperation();
        synchronized (this) {
            btnCancel.setDisable(true);
            timeout.progressProperty().bind(new SimpleDoubleProperty(0));
            instructionTxt.setText("");
        }
        gameController.endTurn(ownerNameOfTheView);
    }
    //endregion

    private synchronized void initializeMatrixButtons(GridPane matrixPane, String prefixTag){
        StyleBooster.forPatternCard(matrixPane, 15);
        // set button of pattern
        for (int row = 0; row < Settings.instance().getMATRIX_NR_ROW(); row++) {
            for (int col = 0; col < Settings.instance().getMATRIX_NR_COL(); col++) {
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
                    e.printStackTrace();
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
    private synchronized void showPickToolCardIndex(String message) {
        enableOnly(ID_TOOLCARDBOX, message);
    }
    //endregion

    //region parameter acquirer

    @Override
    public Die getDieFromDraft(String message) throws InterruptedException, UserInterruptActionException {
        enableOnly(ID_DRAFTPOOL,message);
        return getDie();
    }

    @Override
    public Die getDieFromRound(String message) throws InterruptedException, UserInterruptActionException {
        enableOnly(ID_ROUNDTRACKER,message);
        return getDie();
    }

    @Override
    public Coordinate getCoordinate(String message) throws InterruptedException, UserInterruptActionException {
        enableOnly(ID_MATRIX,message);
        return getCoord();
    }

    @Override
    public int getValue(String message, Integer... values) throws InterruptedException, UserInterruptActionException {
        showPickValues(message, values);
        return getValue();
    }


    //endregion

    public void setGameController(IController gameController) {
        this.gameController = gameController;
    }

    private void displayError(Exception ex){
        //TODO: display graphical effor messagebox
        ex.printStackTrace();
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


