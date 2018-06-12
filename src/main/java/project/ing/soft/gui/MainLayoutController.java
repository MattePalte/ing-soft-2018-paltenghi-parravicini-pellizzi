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
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.ing.soft.Settings;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.cards.WindowPatternCard;
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
    private boolean stopResponding = false;
    private transient PrintStream out;
    private ArrayList<WindowPattern> possiblePatterns;
    private ArrayList<WindowPatternCard> possiblePatternCard;
    private int currentIndexPatternDisplayed;
    private Stage primaryStage;

    //region Constants
    // default configuration for sockets
    private Map<Colour, String> mapBgColour;
    private Map<Colour, String> mapDieColour;
    private final String STORNG_FOCUS = "#f47a42";
    private final String LIGHT_FOCUS = "#19e4ff";
    private final String NEUTRAL_BG_COLOR = "#c4c4c4";
    private final String WHITE = "#fff";
    private final String CONSTRAIN_TEXT_COLOR = "#b8bab9";
    private final String FX_BACKGROUND = "-fx-background-color:";
    private final int NR_TOOLCARD = 3;
    private double SCREEN_WIDTH;
    private double CELL_DIMENSION;
    private double SMALL_CELL_DIMENSION;

    private final String ID_DRAFTPOOL = "draftPool";
    private final String ID_MATRIX = "matrixPane";
    private final String ID_ROUNDTRACKER = "roundtracker";
    private final String ID_TOOLCARDBOX = "toolcardBox";
    private final String ID_BOX_VALUE = "valueBox";
    private final String ID_BOX_ACTION = "main_actions";
    private final String AREA_IDS[] = {ID_DRAFTPOOL, ID_ROUNDTRACKER, ID_MATRIX, ID_TOOLCARDBOX, ID_BOX_ACTION};
    //endregion

    //region Retrieving parameters methods
    private transient ExecutorService turnExecutor;
    private List<Object> parameters = new ArrayList<>();
    private transient Future waitingForParameters;

    public void put(Object obj) {
        synchronized (parameters) {
            parameters.add(obj);
            parameters.notifyAll();
        }
    }

    public Object getObj() throws InterruptedException {
        synchronized (parameters){
            while (parameters.isEmpty()) parameters.wait();
            Object objRemoved = parameters.remove(0);
            parameters.notifyAll();
            return objRemoved;
        }
    }

    public Coordinate getCoord() throws InterruptedException {
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

    public void collectCoordinate(Coordinate coord){
        put(coord);
    }

    public void collectDie(Die die){
        put(die);
    }

    public void collectValue(Integer value){
        put(value);
    }

    public void collectToolcardIndex(Integer index) {
        put(index);
    }


    //endregion

    @FXML
    protected void initialize(){
        // Initialize maps for conversion between code and resources
        mapBgColour = new HashMap<>();
        mapBgColour.put(Colour.BLUE, "#4286f4");
        mapBgColour.put(Colour.VIOLET, "#b762fc");
        mapBgColour.put(Colour.RED, "#fc5067");
        mapBgColour.put(Colour.GREEN, "#6af278");
        mapBgColour.put(Colour.YELLOW, "#f5f97a");
        mapBgColour.put(Colour.WHITE, "#ffffff");

        mapDieColour = new HashMap<>();
        mapDieColour.put(Colour.BLUE, "#033e9e");
        mapDieColour.put(Colour.VIOLET, "#4a0284");
        mapDieColour.put(Colour.RED, "#a80200");
        mapDieColour.put(Colour.GREEN, "#278431");
        mapDieColour.put(Colour.YELLOW, "#92961e");
        mapDieColour.put(Colour.WHITE, "#ffffff");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        SCREEN_WIDTH = primaryScreenBounds.getWidth();
        //SCREEN_WIDTH = Settings.instance().getMIN_SCREEN_SIZE();
        CELL_DIMENSION = SCREEN_WIDTH/30;
        SMALL_CELL_DIMENSION = CELL_DIMENSION/2;

        mainScrollPane.setMaxHeight(primaryScreenBounds.getHeight());
        mainScrollPane.setMinHeight(primaryScreenBounds.getWidth());
        roundtrackerScrollPane.setMinHeight(SMALL_CELL_DIMENSION + 30);
        roundtrackerScrollPane.setMaxHeight(SMALL_CELL_DIMENSION + 30);
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
    @FXML private GridPane matrixPane;

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

    }

    //region Event Responding
    @Override
    public void respondTo(ToolcardActionRequestEvent event) {
        // PRE-SETUP
        initializeButtons();
        endingOperation();
        btnCancel.setDisable(false);
        IToolCardParametersAcquirer myAcquirer = this;
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        waitingForParameters = turnExecutor.submit(
                new Runnable() {
                    private IToolCardParametersAcquirer acquirer;
                    {
                        this.acquirer = myAcquirer;
                    }
                    @Override
                    public void run() {
                        try {

                            ToolCard chosenToolCard = event.getCard();
                            chosenToolCard.fill(acquirer);
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
        displayPrivateObjective(getPrimaryStage().getScene());
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
        String s = "";
        Map<String, String> pointsDescriptor = event.getPointsDescriptor();
        for(Player p : localCopyOfTheStatus.getPlayerList()){
            s += (pointsDescriptor.get(p.getName()));
        }
        for (Pair<Player, Integer> aPair : event.getRank()){
            s += aPair.getKey().getName() + " => " + aPair.getValue() + "\n";
            out.println(aPair.getKey() + " => " + aPair.getValue());
        }
        alert.setHeaderText(s);
        alert.show();
        out.println(s);
        stopResponding = true;
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
        initializeButtons();
        disableAll();
        if (localCopyOfTheStatus.getFavours().get(ownerNameOfTheView) != null) {
            int favoursLeft = localCopyOfTheStatus.getFavours().get(ownerNameOfTheView);
            favourField.setText(String.valueOf(favoursLeft));
        }
    }

    @Override
    public void respondTo(MyTurnEndedEvent event) {
        endingOperation();
        out.println("Timeeeer");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Timer expired");
        String s ="Turn finished";
        alert.setHeaderText(s);
        alert.show();
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
        WindowPattern wndPtrn = null;
        Die[][] placedDie = null;
        Constraint[][] constraints = null;
        // get stuff of the owner of the view
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                wndPtrn = p.getPattern();
                placedDie = p.getPlacedDice();
            }
        }
        // if no pattern setted yet -> draw nothing
        if (wndPtrn == null) return;
        constraints = wndPtrn.getConstraintsMatrix();
        Scene scene = getPrimaryStage().getScene();
        // print die and constraint
        for (int row = 0 ; row < wndPtrn.getHeight(); row++) {
            for (int col = 0 ; col < wndPtrn.getWidth(); col++) {
                Button currentCell = (Button) scene.lookup("#pos" + row + col);
                Die currentDie = placedDie[row][col];
                Constraint constraint = constraints[row][col];
                if (currentDie == null ) {
                    if (!constraint.getImgPath().equals("")) {
                        Image image = new Image(constraint.getImgPath());
                        ImageView bg = new ImageView(image);
                        bg.setFitHeight(CELL_DIMENSION);
                        bg.setFitWidth(CELL_DIMENSION);
                        bg.setPreserveRatio(true);
                        bg.setSmooth(true);
                        bg.setCache(true);
                        currentCell.setGraphic(bg);
                    } else {
                        ImageView bg = new ImageView();
                        bg.setFitHeight(CELL_DIMENSION);
                        bg.setFitWidth(CELL_DIMENSION);
                        currentCell.setGraphic(bg);

                    }
                    currentCell.setStyle(FX_BACKGROUND + mapBgColour.get(constraint.getColour()));
                } else {
                    if (!currentDie.getImgPath().equals("")) {
                        Image image = new Image(currentDie.getImgPath());
                        ImageView bg = new ImageView(image);
                        bg.setFitHeight(CELL_DIMENSION);
                        bg.setFitWidth(CELL_DIMENSION);
                        bg.setPreserveRatio(true);
                        bg.setSmooth(true);
                        bg.setCache(true);
                        currentCell.setGraphic(bg);
                    }
                    currentCell.setStyle(FX_BACKGROUND + mapBgColour.get(constraint.getColour()));
                }
            }
        }



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
            bg.setFitHeight(SMALL_CELL_DIMENSION);
            bg.setFitWidth(SMALL_CELL_DIMENSION);
            bg.setPreserveRatio(true);
            bg.setSmooth(true);
            bg.setCache(true);
            // Create Button
            Button currentCell = new Button();
            paneDraft.add(currentCell, pos,0);
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
        String listOfTurn = "";
        for (Player p : localCopyOfTheStatus.getCurrentTurnList()) {
            listOfTurn += p.getName() + " > ";
        }
        if (roundTracker.getCurrentRound() < Settings.instance().getNrOfRound()) {
            listOfTurn += "round " + (roundTracker.getCurrentRound() + 1);
        } else {
            listOfTurn += "end";
        }
        turnlist.setText(listOfTurn);
        // Draw content of the roundtracker
        Scene scene = getPrimaryStage().getScene();
        GridPane paneRoundTracker = (GridPane) scene.lookup("#" + ID_ROUNDTRACKER);
        //roundtrackerScrollPane = new ScrollPane(paneRoundTracker);
        //roundtrackerScrollPane.setFitToWidth(true);
        List<Die> diceLeft = roundTracker.getDiceLeftFromRound();
        paneRoundTracker.getChildren().clear();
        for (int pos = 0 ; pos < diceLeft.size(); pos++) {
            int row = pos/9;
            int col = pos%9;
            Die currentDie = diceLeft.get(pos);
            // Create Image
            Image image = new Image(currentDie.getImgPath());
            ImageView bg = new ImageView(image);
            bg.setFitHeight(SMALL_CELL_DIMENSION);
            bg.setFitWidth(SMALL_CELL_DIMENSION);
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
        //roundtrackerScrollPane.setContent(paneRoundTracker);
    }
    private synchronized void displayPrivateObjective(Scene scene) {
        ImageView ivPrivateObj = ElementCreator.createCard(myPlayer.getPrivateObjective(), (int) SCREEN_WIDTH/6);
        System.out.println(imgPrivateObjective.getId());
        imgPrivateObjective.getChildren().add(0, ivPrivateObj);
        imgPrivateObjective.setMaxHeight(SCREEN_WIDTH/6);
        StyleBooster.forObjectiveCard(imgPrivateObjective, 5);
    }
    private synchronized void displayToolCard(Scene scene) {
        List<ToolCard> tCard = localCopyOfTheStatus.getToolCards();
        for (int index = 0; index<3; index++) {
            ToolCard currentTool = tCard.get(index);
            ImageView iv = ElementCreator.createCard(currentTool, (int) SCREEN_WIDTH/6);
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
        List<Player> listOfPlayer = localCopyOfTheStatus.getPlayerList();
        if (localCopyOfTheStatus == null || listOfPlayer == null) return;
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
            ImageView iv = ElementCreator.createCard(currentPub, (int) SCREEN_WIDTH/6);
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
        initializeButtons();
        endingOperation();
        synchronized (this) {
            btnCancel.setDisable(false);
        }
        IToolCardParametersAcquirer myAcquirer = this;
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        waitingForParameters = turnExecutor.submit(
                new Runnable() {
                    private IToolCardParametersAcquirer acquirer;
                    {
                        this.acquirer = myAcquirer;
                    }
                    @Override
                    public void run() {
                        try {
                            Coordinate chosenCoord = getCoordinate("Choose a position to place the Die");
                            Die chosenDie = getDieFromDraft("Chose a die to place");
                            disableAll();
                            System.out.println("owner name : " + getOwnerNameOfTheView());
                            System.out.println("gameController : " + gameController);
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
                }
        );
    }

    public void btnPlayToolCardOnCLick() throws Exception {
        // PRE-SETUP
        initializeButtons();
        endingOperation();
        synchronized (this) {
            btnCancel.setDisable(false);
        }
        IToolCardParametersAcquirer myAcquirer = this;
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        waitingForParameters = turnExecutor.submit(
                new Runnable() {
                    private IToolCardParametersAcquirer acquirer;
                    {
                        this.acquirer = myAcquirer;
                    }
                    @Override
                    public void run() {
                        try {
                            showPickToolCardIndex("Choose a toolcard to use");
                            Integer chosenIndex = getValue();
                            ToolCard chosenToolCard = localCopyOfTheStatus.getToolCards().get(chosenIndex);
                            chosenToolCard.fill(acquirer);
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
        btnCancel.setDisable(true);
        timeout.progressProperty().bind(new SimpleDoubleProperty(0));
        gameController.endTurn(ownerNameOfTheView);
        synchronized (this) {
            instructionTxt.setText("");
        }
    }
    //endregion

    public synchronized void initializeButtons(){
        Scene scene = getPrimaryStage().getScene();
        StyleBooster.forPatternCard(matrixPane, 3);
        // set button of pattern
        for (int row = 0; row < Settings.instance().getMATRIX_NR_ROW(); row++) {
            for (int col = 0; col < Settings.instance().getMATRIX_NR_COL(); col++) {
                Button currentCell = (Button) scene.lookup("#pos" + row + col);
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
            //TODO: implement CSS class change
            pane.setStyle(FX_BACKGROUND + LIGHT_FOCUS);
            for (Node n : pane.getChildren()) {
                n.setDisable(false);
            }
        } else {
            pane.setStyle(FX_BACKGROUND + NEUTRAL_BG_COLOR);
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

    public synchronized void disableAll(){
        for (String id : AREA_IDS) {
            focusOff(id);
        }
    }

    public synchronized void enableOnly(String idPane, String message) {
        for (String id : AREA_IDS) {
            if (id.equals(idPane)) {
                focusOn(id, message);
            } else {
                focusOff(id);
            }
        }
    }

    public synchronized void enableActions(){
        btnPlaceDie.setDisable(false);
        btnEndTurn.setDisable(false);
        btnPlayToolCard.setDisable(false);
        btnCancel.setDisable(true);
        focusOn(ID_BOX_ACTION, "Select an action ");
    }
    //endregion


    //region Possile View Interface
    public synchronized void showPickValues(String message, Integer... values) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/layout/chose_value.fxml"));
                Parent root1 = null;
                try {
                    root1 = (Parent) fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
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
    public synchronized void showPickToolCardIndex(String message) {
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
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exeption");
                alert.setHeaderText("Information Alert");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });
    }

}


