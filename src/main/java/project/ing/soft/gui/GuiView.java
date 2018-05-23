package project.ing.soft.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemanager.events.*;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.socket.ControllerProxyOverSocket;
import project.ing.soft.view.IView;

import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GuiView extends UnicastRemoteObject implements IView, IEventHandler, IToolCardFiller,  Serializable{
    private IGameManager localCopyOfTheStatus;
    private String ownerNameOfTheView;
    private transient IController myController;
    private boolean stopResponding = false;
    private transient PrintStream out;
    private final transient Queue<Event> eventsReceived;
    private ArrayList<WindowPattern> possiblePatterns;
    private ArrayList<WindowPatternCard> possiblePatternCard;
    private int currentIndexPatternDisplayed;
    private Stage primaryStage;

    //region Constants
    // default configuration for sockets
    private final String HOST = "localhost";
    private final int PORT = 3000;
    private Map<Colour, String> mapBgColour;
    private Map<Colour, String> mapDieColour;
    private final String STORNG_FOCUS = "#f47a42";
    private final String LIGHT_FOCUS = "#c4fff0";
    private final String NEUTRAL_BG_COLOR = "#c4c4c4";
    private final String WHITE = "#fff";
    private final String CONSTRAIN_TEXT_COLOR = "#b8bab9";
    private final String FX_BACKGROUND = "-fx-background-color:";
    private final int MATRIX_NR_ROW = 4;
    private final int MATRIX_NR_COL = 5;
    private final int NR_TOOLCARD = 3;
    private double SCREEN_WIDTH;
    private double CELL_DIMENSION;
    private double SMALL_CELL_DIMENSION;

    private final String ID_DRAFTPOOL = "draftPool";
    private final String ID_MATRIX = "matrix";
    private final String ID_ROUNDTRACKER = "roundtracker";
    private final String ID_TOOLCARDBOX = "toolcardBox";
    private final String ID_BOX_VALUE = "valueBox";
    private final String AREA_IDS[] = {ID_DRAFTPOOL, ID_ROUNDTRACKER, ID_MATRIX, ID_TOOLCARDBOX};
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
        CELL_DIMENSION = SCREEN_WIDTH/23;
        SMALL_CELL_DIMENSION = CELL_DIMENSION/1.5;

        turnExecutor = Executors.newSingleThreadExecutor();
        IEventHandler eventHandler = this;

        // start long running task to execute events

        Task task = new Task<Void>() {
            @Override public Void call() throws InterruptedException {
                Event toRespond = null;

                while(!isCancelled()) {
                    synchronized (eventsReceived) {
                        try {
                            while (eventsReceived.isEmpty())
                                eventsReceived.wait();
                            toRespond = eventsReceived.remove();
                        } catch (InterruptedException e) {
                            displayError(e);
                        }
                    }
                    if (toRespond != null) {
                        Platform.runLater(new EventRunnable(toRespond, eventHandler));
                        updateMessage("Last event: " + toRespond.toString());
                    }
                    toRespond = null;
                    synchronized (eventsReceived) {
                        eventsReceived.notifyAll();
                    }
                }
                return null;
            }
        };

        status.textProperty().bind(task.messageProperty());
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    //region Getter e Setter
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public String getOwnerNameOfTheView() {
        return ownerNameOfTheView;
    }

    //endregion

    //region GUI Elements
    // PHASE 0 - OPEN CONNECTION
    @FXML private Button btnGetController;
    @FXML private Button btnRmiConnection;
    @FXML private Button btnSocketConnection;
    @FXML private Button btnJoin;
    @FXML private TextField txtName;
    @FXML private TextField favourField;

    // PHASE 1 - CHOOSE PATTERN CARD
    @FXML private Button btnNext;
    @FXML private Button btnPrev;
    @FXML private Button btnChoose;
    @FXML private VBox choosePatternCardBox;

    // TO DESPLAY INFO
    @FXML private Text status;
    @FXML private Text instructionBox;
    @FXML private GridPane matrixPane;

    // PHASE 2 - ACTIONS
    @FXML private Button btnPlaceDie;
    @FXML private Button btnPlayToolCard;
    @FXML private Button btnEndTurn;
    @FXML private Button btnCancel;
    //endregion

    public GuiView() throws RemoteException {
        eventsReceived = new LinkedList<>();
    }

    //region Event Responding
    @Override
    public void respondTo(PlaceThisDieEvent event) {
        // PRE-SETUP
        initializeButtons();
        endingOperation();
        btnCancel.setDisable(false);
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        GuiView myView = this;
        waitingForParameters = turnExecutor.submit(
                new Runnable() {
                    private GuiView gView;
                    {
                        this.gView = myView;
                    }
                    @Override
                    public void run() {
                        Coordinate chosenPosition = null;
                        Die toBePlaced = event.getToBePlaced();
                        ArrayList<Coordinate> compatiblePositions = event.getCompatiblePositions(toBePlaced);

                        try {
                            if (event.getIsValueChoosable()) {
                                int[] values = {1,2,3,4,5,6};
                                showPickValues("You draft this die: " + toBePlaced + " Choose the die value", values);
                                int newValue = getValue();
                                toBePlaced = new Die(newValue, toBePlaced.getColour());
                                myController.chooseDie(toBePlaced);
                                // Needed to let player see the die chosen in the draftpool even if the modelChangedEvent has not been handled yet
                                localCopyOfTheStatus.addToDraft(toBePlaced);
                            }
                        } catch(Exception e){
                            displayError(e);
                        }

                        if(!compatiblePositions.isEmpty()) {
                            try {
                                showPickCoordinate("Choose a position where to place this die: " + toBePlaced);
                                //TODO: enable restriction to compatible position only
                                chosenPosition = getCoord();
                            } catch (InterruptedException e) {
                                displayError(e);
                            }
                            try {
                                myController.placeDie(ownerNameOfTheView, toBePlaced, chosenPosition.getRow(), chosenPosition.getCol());
                            } catch (Exception e) {
                                displayError(e);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        // not used event
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        // nothing special to do
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        out.println("Game finished!");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Final Rank:");
        String s = "";
        for (Pair<Player, Integer> aPair : event.getRank()){
            s += aPair.getKey().getName() + " => " + aPair.getValue() + "\n";
            out.println(aPair.getKey() + " => " + aPair.getValue());
        }
        alert.setHeaderText(s);
        alert.show();
        stopResponding = true;
    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        // show log message
        out.println("gestione di pattern card distributed");
        // get info from event object
        possiblePatterns = new ArrayList<>();
        possiblePatterns.add(event.getOne().getFrontPattern());
        possiblePatterns.add(event.getOne().getRearPattern());
        possiblePatterns.add(event.getTwo().getFrontPattern());
        possiblePatterns.add(event.getTwo().getRearPattern());
        possiblePatternCard = new ArrayList<>();
        possiblePatternCard.add(event.getOne());
        possiblePatternCard.add(event.getTwo());
        showPickPattern();
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        btnPlaceDie.setDisable(false);
        btnPlayToolCard.setDisable(false);
        btnEndTurn.setDisable(false);
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();
        out.println("gestione di model changed");
        drawMySituation();
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
        btnPlaceDie.setDisable(true);
        btnPlayToolCard.setDisable(true);
        btnEndTurn.setDisable(true);
        if(waitingForParameters != null && !waitingForParameters.isDone())
            waitingForParameters.cancel(true);
    }

    //region Draw Things
    private synchronized void displayPatternCard(WindowPattern pattern, Scene scene){
        for (int row = 0 ; row < pattern.getHeight(); row++) {
            for (int col = 0 ; col < pattern.getWidth(); col++) {

                Button currentCell = (Button) scene.lookup("#pos" + row + col);
                Constraint constraint = pattern.getConstraintsMatrix()[row][col];
                if (constraint != null && constraint.getImgPath() != "") {
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
                if (constraint != null)
                    currentCell.setStyle(FX_BACKGROUND + mapBgColour.get(constraint.getColour()));
            }

        }
        favourField.setText(String.valueOf(pattern.getDifficulty()));
    }

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
                    if (constraint.getImgPath() != "") {
                        Image image = new Image(constraint.getImgPath());
                        ImageView bg = new ImageView(image);
                        bg.setFitHeight(CELL_DIMENSION);
                        bg.setFitWidth(CELL_DIMENSION);
                        bg.setPreserveRatio(true);
                        bg.setSmooth(true);
                        bg.setCache(true);
                        currentCell.setGraphic(bg);
                    }
                    currentCell.setStyle(FX_BACKGROUND + mapBgColour.get(constraint.getColour()));
                } else {
                    if (currentDie.getImgPath() != "") {
                        Image image = new Image(currentDie.getImgPath());
                        ImageView bg = new ImageView(image);
                        bg.setFitHeight(SCREEN_WIDTH / 23);
                        bg.setFitWidth(SCREEN_WIDTH / 23);
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
        //TODO: add button to draftpool programmatically as in roundtracker
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
                @Override public void handle(ActionEvent e) {
                    disableAll();
                    collectDie(new Die(currentDie));
                }
            });
        }
    }
    private synchronized void drawValues(Scene scene, String idPane, int[] values) {
        GridPane pane = (GridPane) scene.lookup("#" + idPane);
        pane.getChildren().clear();
        for (int pos = 0 ; pos < values.length; pos++) {
            int currentValue = values[pos];
            // Create Button
            Button currentCell = new Button();
            pane.add(currentCell, pos,0);
            currentCell.setStyle(FX_BACKGROUND + WHITE);
            currentCell.setText(Integer.toString(currentValue));
            currentCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    disableAll();
                    Stage currentStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
                    currentStage.close();
                    collectValue(currentValue);
                }
            });
        }
    }

    private synchronized void drawRoundTracker() {
        Scene scene = getPrimaryStage().getScene();
        GridPane paneRoundTracker = (GridPane) scene.lookup("#" + ID_ROUNDTRACKER);
        List<Die> diceLeft = localCopyOfTheStatus.getRoundTracker().getDiceLeftFromRound();
        paneRoundTracker.getChildren().clear();
        for (int pos = 0 ; pos < diceLeft.size(); pos++) {
            int row = pos/9;
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
            paneRoundTracker.add(currentCell, pos, row);
            currentCell.setGraphic(bg);
            currentCell.setStyle(FX_BACKGROUND + WHITE);
            currentCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    disableAll();
                    collectDie(new Die(currentDie));
                }
            });
        }
    }

    private synchronized void displayPrivateObjective(Scene scene) {
        ImageView ivPrivateObj = (ImageView) scene.lookup("#imgPrivateObjective");
        Image img = null;
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView))
                img = new Image(p.getPrivateObjective().getImgPath());
        }
        if (img == null) img = new Image("objectives/private/30%/objectives-12.png");
        ivPrivateObj.setImage(img);
        ivPrivateObj.setFitHeight(SCREEN_WIDTH/6);
        ivPrivateObj.setPreserveRatio(true);
        ivPrivateObj.setSmooth(true);
        ivPrivateObj.setCache(true);
    }

    private synchronized void displayToolCard(Scene scene) {
        List<ToolCard> tCard = localCopyOfTheStatus.getToolCards();
        for (int i = 0; i<3; i++) {
            ImageView iv = (ImageView) scene.lookup("#toolcard" + i);
            Image img = new Image(tCard.get(i).getImgPath());
            iv.setImage(img);
            iv.setFitHeight(SCREEN_WIDTH/6);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(true);
        }
    }

    private synchronized void displayPublicCard(Scene scene) {
        List<PublicObjective> pubCard = localCopyOfTheStatus.getPublicObjective();
        for (int i = 0; i<3; i++) {
            ImageView iv = (ImageView) scene.lookup("#publicCard" + i);
            Image img = new Image(pubCard.get(i).getImgPath());
            iv.setImage(img);
            iv.setFitHeight(SCREEN_WIDTH/6);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(true);
        }
    }
    //endregion

    //region IView interface
    @Override
    public void update(Event event) throws RemoteException {

        out.println( getTime() + " - " + ownerNameOfTheView + " ha ricevuto un evento :" + event);

        if (!stopResponding) {
            synchronized (eventsReceived) {
                eventsReceived.add(event);
                eventsReceived.notifyAll();
            }
        }
    }

    @Override
    public void attachController(IController gameController) {
        this.myController = gameController;
    }

    @Override
    public void run() {

    }

    private String getTime() {
        Calendar c = Calendar.getInstance(); //automatically set to current time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(c.getTime()).toString();
    }
    //endregion

    //region Fixed Button Handling
    public void btnNextOnCLick(ActionEvent actionEvent) {
        if (currentIndexPatternDisplayed < possiblePatterns.size()-1) {
            currentIndexPatternDisplayed++;
        } else {
            currentIndexPatternDisplayed = 0;
        }
        // HOW TO GET stage from action event - add ActionEvent actionEvent as parameter
        //Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene currentScene = (Scene) ((Node)actionEvent.getSource()).getScene();
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), currentScene);
    }
    public void btnPrevOnCLick(ActionEvent actionEvent) {
        if (currentIndexPatternDisplayed > 0) {
            currentIndexPatternDisplayed--;
        } else {
            currentIndexPatternDisplayed = possiblePatterns.size() - 1;
        }
        Scene currentScene = (Scene) ((Node)actionEvent.getSource()).getScene();
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), currentScene);
    }
    public void btnChooseOnCLick() throws Exception {
        WindowPattern chosenPattern = possiblePatterns.get(currentIndexPatternDisplayed);
        for (WindowPatternCard c : possiblePatternCard) {
            if (c.getFrontPattern() == chosenPattern) {
                myController.choosePattern(ownerNameOfTheView, c, false);
                break;
            }
            if (c.getRearPattern() == chosenPattern) {
                myController.choosePattern(ownerNameOfTheView, c, true);
                break;
            }
        }
        btnNext.setDisable(true);
        btnPrev.setDisable(true);
        btnChoose.setDisable(true);
        return;
    }

    public void btnPlaceDieOnCLick() throws Exception {
        // PRE-SETUP
        initializeButtons();
        endingOperation();
        btnCancel.setDisable(false);
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        GuiView myView = this;
        waitingForParameters = turnExecutor.submit(
            new Runnable() {
                private GuiView gView;
                {
                    this.gView = myView;
                }
                @Override
                public void run() {
                    try {
                        showPickCoordinate("Choose a position to place the Die");
                        Coordinate chosenCoord = getCoord();
                        showPickDieDraft("Chose a die to place");
                        Die chosenDie = getDie();
                        gView.disableAll();
                        gView.myController.placeDie(gView.getOwnerNameOfTheView(), chosenDie, chosenCoord.getRow(), chosenCoord.getCol());
                    } catch (InterruptedException e) {
                        displayError(e);
                        gView.out.println( getTime() + " - " + "Interrupted place die of " + ownerNameOfTheView);
                    } catch (Exception e) {
                        displayError(e);
                        btnPlaceDie.setDisable(false);
                    }
                }
            }
        );
    }

    public void btnPlayToolCardOnCLick() throws Exception {
        // PRE-SETUP
        initializeButtons();
        endingOperation();
        btnCancel.setDisable(false);
        // CREATE AN ANONYMOUS THREAD WAITING FOR INPUT
        GuiView myView = this;
        waitingForParameters = turnExecutor.submit(
            new Runnable() {
                private GuiView gView;
                {
                    this.gView = myView;
                }
                @Override
                public void run() {
                    try {
                        showPickToolCardIndex("Choose a toolcard to use");
                        Integer chosenIndex = getValue();
                        ToolCard chosenToolCard = localCopyOfTheStatus.getToolCards().get(chosenIndex);
                        chosenToolCard.fill(gView);
                        gView.myController.playToolCard(ownerNameOfTheView, chosenToolCard);
                    } catch (InterruptedException e) {
                        displayError(e);
                        out.println("Interrupted play toolcard of " + ownerNameOfTheView);
                    } catch (Exception e) {
                        displayError(e);
                        btnPlayToolCard.setDisable(false);
                    }
                }
            }
        );
    }

    public void btnCancelOnCLick() throws Exception{
        endingOperation();
        btnEndTurn.setDisable(false);
        btnPlayToolCard.setDisable(false);
        btnPlaceDie.setDisable(false);
        btnCancel.setDisable(true);
        synchronized (this) {
            instructionBox.setText("");
        }
    }
    public void btnEndTurnOnCLick() throws Exception {
        endingOperation();
        btnCancel.setDisable(true);
        myController.endTurn(ownerNameOfTheView);
    }
    //endregion




    public synchronized void initializeButtons(){
        Scene scene = getPrimaryStage().getScene();
        // set button of pattern
        for (int row = 0; row < MATRIX_NR_ROW; row++) {
            for (int col = 0; col < MATRIX_NR_COL; col++) {
                Button currentCell = (Button) scene.lookup("#pos" + row + col);
                int finalRow = row;
                int finalCol = col;
                currentCell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        disableAll();
                        collectCoordinate(new Coordinate(finalRow, finalCol));
                    }
                });
            }
        }
        //set listeners on toolcard
        for(int index = 0; index < NR_TOOLCARD; index++) {
            ImageView imageView = (ImageView) scene.lookup("#toolcard" + index);
            int finalIndex = index;
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    disableAll();
                    collectToolcardIndex(finalIndex);
                }
            });
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
        instructionBox.setText(message);
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
    //endregion


    //region Possile View Interface
    public synchronized void showPickCoordinate(String message) {
        enableOnly(ID_MATRIX,message);
    }
    public synchronized void showPickDieDraft(String message) {
        enableOnly(ID_DRAFTPOOL,message);
    }
    public synchronized void showPickDieRound(String message) {
        enableOnly(ID_ROUNDTRACKER,message);
    }
    public synchronized void showPickValues(String message, int[] values) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chose_value.fxml"));
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
                drawValues(pickValueScene, ID_BOX_VALUE, values);
            }
        });
    }

    public synchronized void showPickPattern() {
        // show the first pattern
        currentIndexPatternDisplayed = 0;
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), getPrimaryStage().getScene());

        displayPrivateObjective(getPrimaryStage().getScene());
        displayToolCard(getPrimaryStage().getScene());
        displayPublicCard(getPrimaryStage().getScene());
    }
    public synchronized void showPickToolCardIndex(String message) {
        enableOnly(ID_TOOLCARDBOX, message);
    }
    //endregion



    //region Get GameController
    //TODO: create a splash view
    public void btnRmiConnectionOnClick() throws Exception {
        setName();
        Registry registry = LocateRegistry.getRegistry();
        // gets a reference for the remote controller
        myController = (IController) registry.lookup("controller" + registry.list().length);
        System.out.println("GameController retrieved by RMI");
        setController();
        btnRmiConnection.setText("GameController Obtained");
    }

    public void btnSocketConnectionOnClick() throws Exception {
        setName();
        ControllerProxyOverSocket controllerProxy = new ControllerProxyOverSocket(HOST, PORT);
        controllerProxy.start();
        myController = (IController) controllerProxy;
        System.out.println("ControllerProxy created");
        setController();
        btnSocketConnection.setText("GameController Obtained");
    }

    private void setName() throws Exception {
        // get user name
        ownerNameOfTheView = txtName.getText();
        if (ownerNameOfTheView.equals("")) {
            Exception ex = new Exception("no name inserted");
            displayError(ex);
            throw ex;
        }
        System.out.println("Registered in the app with the name: " + ownerNameOfTheView);
        txtName.setDisable(true);
    }

    private void setController() throws Exception{
        // attach the controller to the GuiView
        this.attachController(myController);
        this.run(); //TODO: useless method?
        myController.joinTheGame(ownerNameOfTheView, this);
        btnRmiConnection.setDisable(true);
        btnSocketConnection.setDisable(true);
    }
    //endregion

    private void displayError(Exception ex){
        //TODO: display graphical effor messagebox
        /*out.println("Error:"+ex.getMessage());
        Scanner input = new Scanner(System.in);

        out.println("Do you need stack trace? [y/n]");

        if(input.next().startsWith("y"))
            ex.printStackTrace();*/
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

    //region IToolCardFiller
    @Override
    public void fill(AlesatoreLaminaRame aToolcard) throws InterruptedException {
        try {
            showPickCoordinate("Select the die you want to move:");
            Coordinate startPos = getCoord();
            aToolcard.setStartPosition(startPos);
            showPickCoordinate("Enter an empty cell's position to move it");
            Coordinate endPos = getCoord();
            aToolcard.setEndPosition(endPos);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }

    }

    @Override
    public void fill(DiluentePastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            showPickDieDraft("Choose a die to take back to the dicebag: ");
            Die chosenDie = getDie();
            aToolcard.setChosenDie(chosenDie);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(Lathekin aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            showPickCoordinate("Enter which is the first die you want to move");
            Coordinate firstStart = getCoord();
            showPickCoordinate("Enter an empty cell's position to move it");
            Coordinate firstEnd = getCoord();
            showPickCoordinate("Enter which is the second die you want to move");
            Coordinate secondStart = getCoord();
            showPickCoordinate("Enter an empty cell's position to move it");
            Coordinate secondEnd = getCoord();
            aToolcard.setFirstDieStartPosition(firstStart);
            aToolcard.setFirstDieEndPosition(firstEnd);
            aToolcard.setSecondDieStartPosition(secondStart);
            aToolcard.setSecondDieEndPosition(secondEnd);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(Martelletto aToolcard) throws InterruptedException, UserInterruptActionException {
        // it doesn't need parameters
    }

    @Override
    public void fill(PennelloPastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            showPickDieDraft("Choose a die to roll");
            Die chosenDie = getDie();
            aToolcard.setToRoll(chosenDie);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(PennelloPerEglomise aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            showPickCoordinate("Enter which die you want to move");
            Coordinate startPosition = getCoord();
            aToolcard.setStartPosition(startPosition);
            showPickCoordinate("Enter an empty cell's position to move it");
            Coordinate endPosition = getCoord();
            aToolcard.setEndPosition(endPosition);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(PinzaSgrossatrice aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            int[] values = {-1, +1};
            showPickValues("", values);
            int chosenValue = getValue();
            aToolcard.setToBeIncreased(chosenValue==1);
            showPickDieDraft("Choose a die from draft pool, then you can increase or decrease its value ");
            Die chosenDie = getDie();
            aToolcard.setChoosenDie(chosenDie);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(RigaSughero aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            showPickDieDraft("Choose a die from the draftpool: ");
            Die chosenDie = getDie();
            aToolcard.setChosenDie(chosenDie);
            showPickCoordinate("Choose a position away from other dice: ");
            Coordinate coord = getCoord();
            aToolcard.setPosition(coord);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(StripCutter aToolcard) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(TaglierinaManuale aToolcard) throws InterruptedException, UserInterruptActionException {
        ArrayList<Coordinate> positions = null;
        ArrayList<Coordinate> moveTo = null;
        try {
            showPickDieRound("Choose a die from the roundtracker: ");
            Die chosenDieRound = getDie();
            aToolcard.setDieFromRoundTracker(chosenDieRound);
            positions = new ArrayList<>();
            moveTo = new ArrayList<>();
            for(int i = 0; i < 2; i++){
                showPickCoordinate("Choose the position of a " + chosenDieRound.getColour() + " placed die in your pattern");
                Coordinate startPosition = getCoord();
                showPickCoordinate("Choose where you want to move the die you have just chosen");
                Coordinate endPosition = getCoord();
                positions.add(startPosition);
                moveTo.add(endPosition);
            }
            aToolcard.setDiceChosen(positions);
            aToolcard.setMoveTo(moveTo);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(TaglierinaCircolare aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            showPickDieDraft("Chose from Draft:");
            Die dieDraft = getDie();
            aToolcard.setDieFromDraft(dieDraft);
            showPickDieRound("Chose from RoundTracker:");
            Die dieRound = getDie();
            aToolcard.setDieFromRoundTracker(dieRound);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(TamponeDiamantato aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            showPickDieDraft("Choose a die from the draftpool: ");
            Die chosenDie = getDie();
            aToolcard.setChosenDie(chosenDie);
        } catch (InterruptedException e) {
            displayError(e);
            throw e;
        }
    }

    @Override
    public void fill(TenagliaRotelle aToolcard) throws InterruptedException, UserInterruptActionException {
        // it doesn't need parameters
    }
    //endregion
}

