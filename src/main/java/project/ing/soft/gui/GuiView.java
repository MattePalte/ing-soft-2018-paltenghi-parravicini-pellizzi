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
    Map<Colour, String> mapBgColour;
    Map<Colour, String> mapDieColour;

    private final String STORNG_FOCUS = "#f47a42";
    private final String LIGHT_FOCUS = "#c4fff0";
    private final String NEUTRAL_BG_COLOR = "#c4c4c4";
    private final String CONSTRAIN_TEXT_COLOR = "#b8bab9";
    private double SCREEN_WIDTH;
    private double SCREEN_HEIGHT;
    private double CELL_DIMENSION;
    private final int MATRIX_NR_ROW = 4;
    private final int MATRIX_NR_COL = 5;
    private final int NR_TOOLCARD = 3;

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

    public String getOwnerNameOfTheView() {
        return ownerNameOfTheView;
    }
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

    // PHASE 0 - OPEN CONNECTION
    @FXML
    private Button btnGetController;
    @FXML
    private Button btnJoin;
    @FXML
    private TextField txtName;

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
    private GridPane matrixPane;

    // PHASE 2 - ACTIONS
    @FXML
    private Button btnPlaceDie;
    @FXML
    private Button btnPlayToolCard;
    @FXML
    private Button btnEndTurn;


    public GuiView() throws RemoteException {
    }

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
        initializeButtons();
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("model is changed");
        alert.setHeaderText("Information Alert");
        if (!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)) {
            String s = "It's the turn of " + localCopyOfTheStatus.getCurrentPlayer().getName() + ". Wait for yours.";
            alert.setContentText(s);
        }
        alert.show();*/
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

    private void endingOperation() {
        disableAll();
        btnPlaceDie.setDisable(true);
        btnPlayToolCard.setDisable(true);
        btnEndTurn.setDisable(true);
        if(waitingForParameters != null && !waitingForParameters.isDone())
            waitingForParameters.cancel(true);
        // Remove everithing in the queue
        synchronized (eventsReceived) {
            while (eventsReceived.size()>0)
                eventsReceived.remove(0);
        }
    }

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
                //currentCell.setText(constraint.getXLMescapeEncoding());
                currentCell.setStyle("-fx-background-color:" + mapBgColour.get(constraint.getColour()) + "; -fx-font: 22 monospace;");
                /*int finalRow = row;
                int finalCol = col;
                currentCell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        try {
                            myController.placeDie(ownerNameOfTheView, new Die(5, Colour.BLUE),finalRow, finalCol);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });*/
            }

        }
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
                            //currentCell.setText(constraint.getXLMescapeEncoding());
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
                            //currentCell.setText(currentDie.getXLMescapeEncoding());
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
        List<Die> draft = localCopyOfTheStatus.getDraftPool();
        for (int pos = 0 ; pos < 9; pos++) {
            Button currentCell = (Button) scene.lookup("#draft" + pos);
            if (pos < draft.size() ) {
                Die currentDie = draft.get(pos);
                Image image = new Image(currentDie.getImgPath());
                ImageView bg = new ImageView(image);
                bg.setFitHeight(CELL_DIMENSION);
                bg.setFitWidth(CELL_DIMENSION);
                bg.setPreserveRatio(true);
                bg.setSmooth(true);
                bg.setCache(true);
                currentCell.setGraphic(bg);
                //currentCell.setText(currentDie.getXLMescapeEncoding());
                currentCell.setStyle("-fx-text-fill:" + mapDieColour.get(currentDie.getColour()) +
                        "; -fx-background-color:#FFF; -fx-font: 22 monospace;");
            } else {
                currentCell.setGraphic(null);
                //currentCell.setText(" ");
                currentCell.setStyle("-fx-background-color:" + NEUTRAL_BG_COLOR + "; -fx-font: 22 monospace;");
            }
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

    @Override
    public void update(Event event) throws RemoteException, Exception {

        out.println( getTime() + " - " + ownerNameOfTheView + " ha ricevuto un evento :" + event);

        if (!stopResponding) {
            synchronized (eventsReceived) {
                eventsReceived.add(event);
                eventsReceived.notifyAll();
            }
            //aEvent.accept(this);
        }
    }

    private String getTime() {
        Calendar c = Calendar.getInstance(); //automatically set to current time
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(c.getTime()).toString();
        return time;
    }

    @Override
    public void attachController(IController gameController) throws RemoteException, Exception {
        this.myController = gameController;
    }

    @Override
    public void run() throws Exception {

    }

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
        btnPlaceDie.setDisable(true);
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
        btnPlayToolCard.setDisable(true);
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

    public void btnEndTurnOnCLick(ActionEvent actionEvent) throws Exception {
        endingOperation();
        myController.endTurn(ownerNameOfTheView);
    }

    public synchronized void disableAll(){
        Scene scene = getPrimaryStage().getScene();
        // de-focus draft
        focusOn("draftPool", false);
        // disable button of draft
        for (int pos = 0 ; pos < 9; pos++) {
            Button currentCell = (Button) scene.lookup("#draft" + pos);
            //currentCell.setStyle(currentCell.getStyle().replace("-fx-border-color:" + STORNG_FOCUS, ""));
            currentCell.setDisable(true);
        }
        // de-focus pattern
        focusOn("matrix", false);
        // disable button of pattern
        for (int row = 0; row < MATRIX_NR_ROW; row++) {
            for (int col = 0; col < MATRIX_NR_COL; col++) {
                Button currentCell = (Button) scene.lookup("#pos" + row + col);
                //currentCell.setStyle(currentCell.getStyle().replace("-fx-border-color:" + STORNG_FOCUS, ""));
                currentCell.setDisable(true);
            }
        }
        // de-focus toolcard Box
        focusOn("toolcardBox", false);
        // disable button of pattern
        for (int index = 0; index < NR_TOOLCARD; index++) {
            ImageView imageView = (ImageView) scene.lookup("#toolcard" + index);
            imageView.setDisable(true);
        }
    }

    public synchronized void initializeButtons(){
        Scene scene = getPrimaryStage().getScene();
        // set button of draft
        List<Die> draft = localCopyOfTheStatus.getDraftPool();
        for (int pos = 0 ; pos < 9; pos++) {
            Button currentCell = (Button) scene.lookup("#draft" + pos);
            currentCell.setDisable(true);
            if (pos < draft.size()) {
                Die currentDie = draft.get(pos);
                currentCell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        disableAll();
                        collectDie(new Die(currentDie));
                        //currentCell.setStyle(currentCell.getStyle() + "; -fx-border-color:" + STORNG_FOCUS);
                    }
                });
            } else {
                currentCell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        System.out.println("no action with this button");
                    }
                });
            }
        }
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


    public void collectCoordinate(Coordinate coord){
        put(coord);
    }

    public void collectDie(Die die){
        put(die);
    }

    public void collectToolcardIndex(Integer index) {
        put(index);
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
        }
    }

    public void btnGetControllerOnClick(ActionEvent actionEvent) throws Exception {
        Registry registry = LocateRegistry.getRegistry();

        // gets a reference for the remote controller
        myController = (IController) registry.lookup("controller" + registry.list().length);
        ownerNameOfTheView = txtName.getText();
        System.out.println("Registered in the app with the name: " + ownerNameOfTheView);
        // creates and launches the view
        //myView = new LocalViewCli(txtName.getText());

        this.attachController(myController);
        this.run();
        System.out.println("Controller retrieved");
        btnGetController.setText("Controller obtained");
        btnGetController.setDisable(true);
        btnJoin.setDisable(false);

    }

    public void btnJoinOnClick(ActionEvent actionEvent) throws Exception {
        System.out.println("Sto per accedere al match con nome " + ownerNameOfTheView);
        myController.joinTheGame(ownerNameOfTheView, this);
        btnJoin.setDisable(true);
        btnJoin.setText("You are in the game");

    }

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

    @Override
    public void fill(AlesatoreLaminaRame aToolcard) {
        try {
            disableAll();
            focusOn("matrix", true);
            Coordinate startPos = (Coordinate) getObj();
            aToolcard.setStartPosition(startPos);
            disableAll();
            focusOn("matrix", true);
            Coordinate endPos = (Coordinate) getObj();
            aToolcard.setEndPosition(endPos);
        } catch (InterruptedException e) {
            displayError(e);
        }

    }

    @Override
    public void fill(DiluentePastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {
        
    }

    @Override
    public void fill(Lathekin aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(Martelletto aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(PennelloPastaSalda aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(PennelloPerEglomise aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(PinzaSgrossatrice aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(RigaSughero aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(StripCutter aToolcard) {

    }

    @Override
    public void fill(TaglierinaManuale aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(TaglierinaCircolare aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(TamponeDiamantato aToolcard) throws InterruptedException, UserInterruptActionException {

    }

    @Override
    public void fill(TenagliaRotelle aToolcard) throws InterruptedException, UserInterruptActionException {

    }
}

