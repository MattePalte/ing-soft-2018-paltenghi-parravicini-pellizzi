package project.ing.soft.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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
import project.ing.soft.socket.ControllerProxy;
import project.ing.soft.view.IView;

import java.io.PrintStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GuiView extends UnicastRemoteObject implements IView, IEventHandler, IToolCardFiller,  Serializable{
    private IGameManager localCopyOfTheStatus;
    private String ownerNameOfTheView;
    private IController myController;
    private boolean stopResponding = false;
    private transient PrintStream out;
    private transient Queue<Event> eventsReceived;
    private ArrayList<WindowPattern> possiblePatterns;
    private ArrayList<WindowPatternCard> possiblePatternCard;
    private int currentIndexPatternDisplayed;
    private Stage primaryStage;

    //region Constants
    // default configuration for sockets
    String host = "localhost";
    int port    = 3000;
    Map<Colour, String> mapBgColour;
    Map<Colour, String> mapDieColour;
    private final String STORNG_FOCUS = "#f47a42";
    private final String LIGHT_FOCUS = "#c4fff0";
    private final String NEUTRAL_BG_COLOR = "#c4c4c4";
    private final String CONSTRAIN_TEXT_COLOR = "#b8bab9";
    private final int MATRIX_NR_ROW = 4;
    private final int MATRIX_NR_COL = 5;
    private final int NR_TOOLCARD = 3;
    private double SCREEN_WIDTH;
    private double SCREEN_HEIGHT;
    private double CELL_DIMENSION;
    private double SMALL_CELL_DIMENSION;
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

    public void collectCoordinate(Coordinate coord){
        put(coord);
    }

    public void collectDie(Die die){
        put(die);
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
        SCREEN_HEIGHT = primaryScreenBounds.getHeight();
        CELL_DIMENSION = SCREEN_WIDTH/23;
        SMALL_CELL_DIMENSION = CELL_DIMENSION/1.5;

        turnExecutor = Executors.newSingleThreadExecutor();

        IEventHandler eventHandler = this;

        // start long running task to execute events

        Task task = new Task<Void>() {
            @Override public Void call() {
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

    public void setEventsReceived(Queue<Event> eventsReceived) {
        this.eventsReceived = eventsReceived;
    }

    public String getOwnerNameOfTheView() {
        return ownerNameOfTheView;
    }

    //endregion

    //region GUI Elements
    // PHASE 0 - OPEN CONNECTION
    @FXML
    private Button btnGetController;
    @FXML
    private Button btnRmiConnection;
    @FXML
    private Button btnSocketConnection;
    @FXML
    private Button btnJoin;
    @FXML
    private TextField txtName;
    @FXML
    private TextField favourField;

    // PHASE 1 - CHOOSE PATTERN CARD
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnChoose;
    @FXML
    private VBox choosePatternCardBox;

    // TO DESPLAY INFO
    @FXML
    private Text status;
    @FXML
    private Text instructionBox;
    @FXML
    private GridPane matrixPane;

    // PHASE 2 - ACTIONS
    @FXML
    private Button btnPlaceDie;
    @FXML
    private Button btnPlayToolCard;
    @FXML
    private Button btnEndTurn;
    @FXML
    private Button btnCancel;
    //endregion

    public GuiView() throws RemoteException {
    }

    //region Event Responding
    @Override
    public void respondTo(PlaceThisDieEvent event) {

    }

    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {

    }

    @Override
    public void respondTo(FinishedSetupEvent event) {

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
        // optional msgbox
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pattern Card arrivate");
        alert.setHeaderText("Information Alert");
        String s ="This is an example of JavaFX 8 Dialogs... ";
        alert.setContentText(s);
        alert.show();*/

        // get info from event object
        possiblePatterns = new ArrayList<>();
        possiblePatterns.add(event.getOne().getFrontPattern());
        possiblePatterns.add(event.getOne().getRearPattern());
        possiblePatterns.add(event.getTwo().getFrontPattern());
        possiblePatterns.add(event.getTwo().getRearPattern());
        possiblePatternCard = new ArrayList<>();
        possiblePatternCard.add(event.getOne());
        possiblePatternCard.add(event.getTwo());
        // show the first pattern
        currentIndexPatternDisplayed = 0;
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), getPrimaryStage().getScene());
        displayPrivateObjective(getPrimaryStage().getScene());
        displayToolCard(getPrimaryStage().getScene());
        displayPublicCard(getPrimaryStage().getScene());
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
        int favoursLeft = localCopyOfTheStatus.getFavours().get(ownerNameOfTheView);
        favourField.setText(String.valueOf(favoursLeft));
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
        // Remove everithing in the queue
        //TODO: does it make sense?
        /*synchronized (eventsReceived) {
            while (eventsReceived.size()>0)
                eventsReceived.remove(0);
        }*/
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
                currentCell.setStyle("-fx-background-color:" + mapBgColour.get(constraint.getColour()) + "; -fx-font: 22 monospace;");
            }

        }
        favourField.setText(String.valueOf(pattern.getDifficulty()));
    }

    private synchronized void drawMySituation() {
        for (Player p : localCopyOfTheStatus.getPlayerList()){
            Scene scene = getPrimaryStage().getScene();
            if (p.getName().equals(ownerNameOfTheView)){
                Player currentPlayer = p;
                WindowPattern wndPtrn = p.getPattern();
                Die[][] placedDie = p.getPlacedDice();
                if (wndPtrn == null) return;
                Constraint[][] constraints = wndPtrn.getConstraintsMatrix();
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
                            currentCell.setStyle("-fx-text-fill: " + CONSTRAIN_TEXT_COLOR + " ;"+
                                    "-fx-background-color:" + mapBgColour.get(constraint.getColour()) + "; -fx-font: 22 monospace;");
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
                            currentCell.setStyle("-fx-text-fill:" + mapDieColour.get(currentDie.getColour()) +
                                    "; -fx-background-color:" + mapBgColour.get(constraint.getColour()) + "; -fx-font: 22 monospace;");
                        }
                    }
                }
            }
        }


    }

    private synchronized void drawDraftPool() {
        Scene scene = getPrimaryStage().getScene();
        GridPane paneDraft = (GridPane) scene.lookup("#draftPool");
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
            currentCell.setStyle("-fx-text-fill:" + mapDieColour.get(currentDie.getColour()) +
                    "; -fx-background-color:#FFF; -fx-font: 22 monospace;");
            currentCell.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    disableAll();
                    collectDie(new Die(currentDie));
                }
            });
        }
    }

    private synchronized void drawRoundTracker() {
        Scene scene = getPrimaryStage().getScene();
        GridPane paneRoundTracker = (GridPane) scene.lookup("#roundtracker");
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
            currentCell.setStyle("-fx-text-fill:" + mapDieColour.get(currentDie.getColour()) +
                    "; -fx-background-color:#FFF; -fx-font: 22 monospace;");
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
        //ivPrivateObj.maxHeight(SCREEN_WIDTH/6);
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
            //TODO: fit the screen with toolcard image
            //iv.maxHeight(SCREEN_WIDTH/6);
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
            //iv.maxHeight(SCREEN_WIDTH/6);
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
            //aEvent.accept(this);
        }
    }

    @Override
    public void attachController(IController gameController) throws RemoteException, Exception {
        this.myController = gameController;
    }

    @Override
    public void run() throws Exception {

    }
    private String getTime() {
        Calendar c = Calendar.getInstance(); //automatically set to current time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(c.getTime()).toString();
        return time;
    }
    //endregion

    //region Fixed Button Handling
    public void btnNextOnCLick(ActionEvent actionEvent) {
        if (currentIndexPatternDisplayed < possiblePatterns.size()-1) {
            currentIndexPatternDisplayed++;
        } else {
            currentIndexPatternDisplayed = 0;
        }
        //Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), getPrimaryStage().getScene());
    }
    public void btnPrevOnCLick(ActionEvent actionEvent) {
        if (currentIndexPatternDisplayed > 0) {
            currentIndexPatternDisplayed--;
        } else {
            currentIndexPatternDisplayed = possiblePatterns.size() - 1;
        }
        //Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), getPrimaryStage().getScene());
    }
    public void btnChooseOnCLick(ActionEvent actionEvent) throws Exception {
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

    public void btnPlaceDieOnCLick(ActionEvent actionEvent) throws Exception {
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
                        gView.disableAll();
                        gView.focusOn("matrix", true);
                        Coordinate chosenCoord = (Coordinate) gView.getObj();
                        gView.disableAll();
                        gView.focusOn("draftPool", true);
                        Die chosenDie = (Die) gView.getObj();
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

    public void btnPlayToolCardOnCLick(ActionEvent actionEvent) throws Exception {
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
                        gView.disableAll();
                        gView.focusOn("toolcardBox", true);
                        Integer chosenIndex = (Integer) gView.getObj();
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

    public void btnCancelOnCLick(ActionEvent actionEvent) throws Exception{
        endingOperation();
        btnEndTurn.setDisable(false);
        btnPlayToolCard.setDisable(false);
        btnPlaceDie.setDisable(false);
        btnCancel.setDisable(true);
        synchronized (this) {
            instructionBox.setText("");
        }
    }
    public void btnEndTurnOnCLick(ActionEvent actionEvent) throws Exception {
        endingOperation();
        btnCancel.setDisable(true);
        myController.endTurn(ownerNameOfTheView);
    }
    //endregion

    public synchronized void disableAll(){
        focusOn("draftPool", false);
        focusOn("matrix", false);
        focusOn("roundtracker", false);
        focusOn("toolcardBox", false);
    }

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
                        //currentCell.setStyle(currentCell.getStyle() + "; -fx-border-color:" + STORNG_FOCUS);
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

    private synchronized void focusOn(String idPane, boolean isFocused){
        Scene scene = getPrimaryStage().getScene();
        Pane pane = (Pane) scene.lookup("#" + idPane);
        if (isFocused) {
            //TODO: implement CSS class change
            pane.setStyle("-fx-background-color:" + LIGHT_FOCUS);
            for (Node n : pane.getChildren()) {
                n.setDisable(false);
            }
        } else {
            pane.setStyle("-fx-background-color:" + NEUTRAL_BG_COLOR);
            for (Node n : pane.getChildren()) {
                n.setDisable(true);
            }
        }
    }

    private void focusOn(String idPane, boolean isFocused, String message){
        focusOn(idPane, isFocused);
        synchronized (this) {
            instructionBox.setText(message);
        }
    }

    //region Get Controller
    //TODO: create a splash view
    public void btnRmiConnectionOnClick(ActionEvent actionEvent) throws Exception {
        setName();
        Registry registry = LocateRegistry.getRegistry();
        // gets a reference for the remote controller
        myController = (IController) registry.lookup("controller" + registry.list().length);
        System.out.println("Controller retrieved by RMI");
        setController();
        btnRmiConnection.setText("Controller Obtained");
    }

    public void btnSocketConnectionOnClick(ActionEvent actionEvent) throws Exception {
        setName();
        ControllerProxy controllerProxy = new ControllerProxy(host, port);
        controllerProxy.start();
        myController = (IController) controllerProxy;
        System.out.println("ControllerProxy created");
        setController();
        btnSocketConnection.setText("Controller Obtained");
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
    public void fill(AlesatoreLaminaRame aToolcard) {
        try {
            disableAll();
            focusOn("matrix", true, "Enter which die you want to move");
            Coordinate startPos = (Coordinate) getObj();
            aToolcard.setStartPosition(startPos);
            disableAll();
            focusOn("matrix", true, "Enter an empty cell's position to move it");
            Coordinate endPos = (Coordinate) getObj();
            aToolcard.setEndPosition(endPos);
        } catch (InterruptedException e) {
            displayError(e);
        }

    }

    @Override
    public void fill(DiluentePastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            disableAll();
            focusOn("draftPool", true, "Choose a die to take back to the dicebag: ");
            Die chosenDie = (Die) getObj();
            aToolcard.setChosenDie(chosenDie);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(Lathekin aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            disableAll();
            focusOn("matrix", true, "Enter which is the first die you want to move");
            Coordinate firstStart = (Coordinate) getObj();
            aToolcard.setFirstDieStartPosition(firstStart);
            disableAll();
            focusOn("matrix", true, "Enter an empty cell's position to move it");
            Coordinate firstEnd = (Coordinate) getObj();
            aToolcard.setFirstDieEndPosition(firstEnd);
            disableAll();
            focusOn("matrix", true, "Enter which is the second die you want to move");
            Coordinate secondStart = (Coordinate) getObj();
            aToolcard.setSecondDieStartPosition(secondStart);
            disableAll();
            focusOn("matrix", true, "Enter an empty cell's position to move it");
            Coordinate secondEnd = (Coordinate) getObj();
            aToolcard.setSecondDieEndPosition(secondEnd);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(Martelletto aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(PennelloPastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            disableAll();
            focusOn("draftPool", true, "Choose a die to roll");
            Die chosenDie = (Die) getObj();
            aToolcard.setToRoll(chosenDie);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(PennelloPerEglomise aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            disableAll();
            focusOn("matrix", true, "Enter which die you want to move");
            Coordinate startPosition = (Coordinate) getObj();
            aToolcard.setStartPosition(startPosition);
            disableAll();
            focusOn("matrix", true, "Enter an empty cell's position to move it");
            Coordinate endPosition = (Coordinate) getObj();
            aToolcard.setEndPosition(endPosition);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(PinzaSgrossatrice aToolcard) throws InterruptedException, UserInterruptActionException {
        //TODO: implement msgbox to choose between increase and decrease
    }

    @Override
    public void fill(RigaSughero aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            disableAll();
            focusOn("draftPool", true, "Choose a die from the draftpool: ");
            Die chosenDie = (Die) getObj();
            aToolcard.setChosenDie(chosenDie);
            disableAll();
            focusOn("matrix", true, "Choose a position away from other dice: ");
            Coordinate coord = (Coordinate) getObj();
            aToolcard.setPosition(coord);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(StripCutter aToolcard) {

    }

    @Override
    public void fill(TaglierinaManuale aToolcard) throws InterruptedException, UserInterruptActionException {
        ArrayList<Coordinate> positions = null;
        ArrayList<Coordinate> moveTo = null;
        try {
            disableAll();
            focusOn("roundtracker", true, "Choose a die from the roundtracker: ");
            Die chosenDieRound = (Die) getObj();
            aToolcard.setDieFromRoundTracker(chosenDieRound);
            positions = new ArrayList<>();
            moveTo = new ArrayList<>();
            for(int i = 0; i < 2; i++){
                disableAll();
                focusOn("matrix", true, "Choose the position of a " + chosenDieRound.getColour() + " placed die in your pattern");
                Coordinate startPosition = (Coordinate) getObj();
                disableAll();
                focusOn("matrix", true, "Choose where you want to move the die you have just chosen");
                Coordinate endPosition = (Coordinate) getObj();
                positions.add(startPosition);
                moveTo.add(endPosition);
            }
            aToolcard.setDiceChosen(positions);
            aToolcard.setMoveTo(moveTo);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(TaglierinaCircolare aToolcard) throws InterruptedException, UserInterruptActionException {
        //TODO: implement roundtracker
        try {
            disableAll();
            focusOn("draftPool", true, "Chose from Draft:");
            Die dieDraft = (Die) getObj();
            aToolcard.setDieFromDraft(dieDraft);
            disableAll();
            focusOn("roundtracker", true, "Chose from RoundTracker:");
            Die dieRound = (Die) getObj();
            aToolcard.setDieFromRoundTracker(dieRound);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(TamponeDiamantato aToolcard) throws InterruptedException, UserInterruptActionException {
        try {
            disableAll();
            focusOn("draftPool", true, "Choose a die from the draftpool: ");
            Die chosenDie = (Die) getObj();
            aToolcard.setChosenDie(chosenDie);
        } catch (InterruptedException e) {
            displayError(e);
        }
    }

    @Override
    public void fill(TenagliaRotelle aToolcard) throws InterruptedException, UserInterruptActionException {

    }
    //endregion
}

