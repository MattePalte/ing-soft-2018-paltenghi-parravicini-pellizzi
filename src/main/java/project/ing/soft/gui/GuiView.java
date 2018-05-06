package project.ing.soft.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import project.ing.soft.Colour;
import project.ing.soft.Coordinate;
import project.ing.soft.Die;
import project.ing.soft.Player;
import project.ing.soft.cards.Constraint;
import project.ing.soft.cards.WindowPattern;
import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.cards.objectives.publics.PublicObjective;
import project.ing.soft.cards.toolcards.ToolCard;
import project.ing.soft.controller.IController;
import project.ing.soft.events.*;
import project.ing.soft.events.Event;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.view.IView;

import java.io.PrintStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class GuiView extends UnicastRemoteObject implements IView, IEventHandler, Serializable{
    private IGameManager localCopyOfTheStatus;
    private String ownerNameOfTheView;
    private IController myController;
    private boolean stopResponding = false;
    private transient PrintStream out;
    private transient Queue<Event> eventsReceived;
    private transient ExecutorService turnExecutor;
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

    // parameters to carry out actions
    private Die dieChosenFromDraft;
    private Coordinate coordPlacement;

    public Die getDieChosenFromDraft() {
        return dieChosenFromDraft;
    }

    public void setDieChosenFromDraft(Die dieChosenFromDraft) {
        this.dieChosenFromDraft = dieChosenFromDraft;
    }

    public Coordinate getCoordPlacement() {
        return coordPlacement;
    }

    public void setCoordPlacement(Coordinate coordPlacement) {
        this.coordPlacement = coordPlacement;
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
    private Button btnEndTurn;
    @FXML
    private Button btnConfirm;


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
        // open new scene
        //Stage primaryStage = (Stage) btnGetController.getScene().getWindow();
        /*Parent root = null;
        String sceneFile = "/choose_pattern_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = fxmlLoader.load();
            fxmlLoader.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 400, 400);
        Button btnNext = (Button) scene.lookup("#btnNext");
        btnNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnNextOnCLick(event);
            }
        });
        Button btnPrev = (Button) scene.lookup("#btnPrev");
        btnPrev.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnPrevOnCLick(event);
            }
        });
        Button btnChoose = (Button) scene.lookup("#btnChoose");
        btnChoose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    btnChooseOnCLick(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        primaryStage.setTitle("Sagrada - GUI");
        primaryStage.setScene(scene);*/

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
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), primaryStage.getScene());
        displayPrivateObjective(primaryStage.getScene());
        displayToolCard(primaryStage.getScene());
        displayPublicCard(primaryStage.getScene());
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        btnPlaceDie.setDisable(false);
        btnEndTurn.setDisable(false);
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();
        out.println("gestione di Model changed");
        displayMySituation();
        displayDraftPool();
        btnConfirm.setDisable(true);
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Model is changed");
        alert.setHeaderText("Information Alert");
        if (!localCopyOfTheStatus.getCurrentPlayer().getName().equals(ownerNameOfTheView)) {
            String s = "It's the turn of " + localCopyOfTheStatus.getCurrentPlayer().getName() + ". Wait for yours.";
            alert.setContentText(s);
        }
        alert.show();*/
    }


    private void displayPatternCard(WindowPattern pattern, Scene scene){
        for (int row = 0 ; row < pattern.getHeight(); row++) {
            for (int col = 0 ; col < pattern.getWidth(); col++) {

                Button currentCell = (Button) scene.lookup("#pos" + row + col);
                Constraint constraint = pattern.getConstraintsMatrix()[row][col];
                currentCell.setText(constraint.getXLMescapeEncoding());
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

    private void displayMySituation() {
        for (Player p : localCopyOfTheStatus.getPlayerList()){
            Scene scene = primaryStage.getScene();
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

                        if (currentDie == null) {
                            currentCell.setText(constraint.getXLMescapeEncoding());
                            currentCell.setStyle("-fx-text-fill: " + CONSTRAIN_TEXT_COLOR + " ;"+
                                    "-fx-background-color:" + mapBgColour.get(constraint.getColour()) + "; -fx-font: 22 monospace;");
                        } else {
                            currentCell.setText(currentDie.getXLMescapeEncoding());
                            currentCell.setStyle("-fx-text-fill:" + mapDieColour.get(currentDie.getColour()) +
                                    "; -fx-background-color:" + mapBgColour.get(constraint.getColour()) + "; -fx-font: 22 monospace;");
                        }
                        currentCell.setOnAction(null);
                    }
                }
            }
        }


    }

    private void displayDraftPool() {
        Scene scene = primaryStage.getScene();
        List<Die> draft = localCopyOfTheStatus.getDraftPool();
        for (int pos = 0 ; pos < 9; pos++) {
            Button currentCell = (Button) scene.lookup("#draft" + pos);
            if (pos < draft.size() ) {
                Die currentDie = draft.get(pos);
                currentCell.setDisable(false);
                currentCell.setText(currentDie.getXLMescapeEncoding());
                currentCell.setStyle("-fx-text-fill:" + mapDieColour.get(currentDie.getColour()) +
                        "; -fx-background-color:#FFF; -fx-font: 22 monospace;");
                currentCell.setOnAction(null);
            } else {
                currentCell.setText(" ");
                currentCell.setStyle("-fx-background-color:" + NEUTRAL_BG_COLOR + "; -fx-font: 22 monospace;");
                currentCell.setDisable(true);
            }
        }
    }

    private void displayPrivateObjective(Scene scene) {
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

    private void displayToolCard(Scene scene) {
        List<ToolCard> tCard = localCopyOfTheStatus.getToolCards();
        for (int i = 0; i<3; i++) {
            ImageView iv = (ImageView) scene.lookup("#toolcard" + i);
            Image img = new Image(tCard.get(i).getImgPath());
            iv.setImage(img);
            //iv.maxHeight(SCREEN_WIDTH/6);
            iv.setFitHeight(SCREEN_WIDTH/6);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(true);
        }
    }

    private void displayPublicCard(Scene scene) {
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
        out.println( ownerNameOfTheView + " ha ricevuto un evento :" + event);

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

    public void btnJoinOnClick(ActionEvent actionEvent) throws Exception {
        System.out.println("Sto per accedere al match con nome " + ownerNameOfTheView);
        myController.joinTheGame(ownerNameOfTheView, this);
        btnJoin.setDisable(true);
        btnJoin.setText("You are in the game");

    }
    public void btnNextOnCLick(ActionEvent actionEvent) {
        if (currentIndexPatternDisplayed < possiblePatterns.size()-1) {
            currentIndexPatternDisplayed++;
        } else {
            currentIndexPatternDisplayed = 0;
        }
        //Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), primaryStage.getScene());
    }
    public void btnPrevOnCLick(ActionEvent actionEvent) {
        if (currentIndexPatternDisplayed > 0) {
            currentIndexPatternDisplayed--;
        } else {
            currentIndexPatternDisplayed = possiblePatterns.size() - 1;
        }
        //Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), primaryStage.getScene());
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

    public void btnConfirmOnClick(ActionEvent actionEvent) throws Exception {
        myController.placeDie(ownerNameOfTheView, getDieChosenFromDraft(), getCoordPlacement().getRow(), getCoordPlacement().getCol());
        disableAll();
    }

    public void btnPlaceDieOnCLick(ActionEvent actionEvent) throws Exception {
        focusOnDraft();
        btnConfirm.setDisable(false);
        btnPlaceDie.setDisable(true);
    }
    public void btnPlayToolCardOnCLick(ActionEvent actionEvent) throws Exception {

    }
    public void btnEndTurnOnCLick(ActionEvent actionEvent) throws Exception {
        myController.endTurn(ownerNameOfTheView);
        btnEndTurn.setDisable(true);
        btnPlaceDie.setDisable(true);
    }

    private void disableAll(){
        Scene scene = primaryStage.getScene();
        // de-focus draft
        GridPane gridDraft = (GridPane) scene.lookup("#draftPool");
        gridDraft.setStyle("-fx-background-color:" + NEUTRAL_BG_COLOR);
        // disable button of draft
        List<Die> draft = localCopyOfTheStatus.getDraftPool();
        for (int pos = 0 ; pos < 9; pos++) {
            Button currentCell = (Button) scene.lookup("#draft" + pos);
            if (pos < draft.size()) currentCell.setOnAction(null);
        }
        // de-focus pattern
        VBox boxPatter = (VBox) scene.lookup("#currentPlayerSituation");
        boxPatter.setStyle("-fx-background-color:" + NEUTRAL_BG_COLOR);
        // disable button of pattern
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                Player currentPlayer = p;
                WindowPattern wndPtrn = p.getPattern();
                for (int row = 0; row < wndPtrn.getHeight(); row++) {
                    for (int col = 0; col < wndPtrn.getWidth(); col++) {
                        Button currentCell = (Button) scene.lookup("#pos" + row + col);
                        currentCell.setOnAction(null);
                    }
                }
            }
        }

    }

    private void focusOnDraft() {
        disableAll();
        Scene scene = primaryStage.getScene();
        GridPane gridDraft = (GridPane) scene.lookup("#draftPool");
        gridDraft.setStyle("-fx-background-color:" + LIGHT_FOCUS);
        List<Die> draft = localCopyOfTheStatus.getDraftPool();
        for (int pos = 0 ; pos < 9; pos++) {
            Button currentCell = (Button) scene.lookup("#draft" + pos);
            if (pos < draft.size() ) {
                Die currentDie = draft.get(pos);
                currentCell.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        try {
                            setDieChosenFromDraft(new Die(currentDie));
                            currentCell.setStyle(currentCell.getStyle() + "; -fx-border-color:" + STORNG_FOCUS);
                            focusOnPattern();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        }

    }

    private void focusOnPattern() {
        disableAll();
        Scene scene = primaryStage.getScene();
        VBox boxPatter = (VBox) scene.lookup("#currentPlayerSituation");
        boxPatter.setStyle("-fx-background-color:" + LIGHT_FOCUS);
        for (Player p : localCopyOfTheStatus.getPlayerList()) {
            if (p.getName().equals(ownerNameOfTheView)) {
                Player currentPlayer = p;
                WindowPattern wndPtrn = p.getPattern();
                for (int row = 0; row < wndPtrn.getHeight(); row++) {
                    for (int col = 0; col < wndPtrn.getWidth(); col++) {

                        Button currentCell = (Button) scene.lookup("#pos" + row + col);
                        int finalCol = col;
                        int finalRow = row;
                        currentCell.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                try {
                                    setCoordPlacement(new Coordinate(finalRow, finalCol));
                                    currentCell.setStyle(currentCell.getStyle() + "; -fx-border-color:" + STORNG_FOCUS);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public void btnGetControllerOnClick(ActionEvent actionEvent) throws Exception {
        Registry registry = LocateRegistry.getRegistry();

        // gets a reference for the remote controller
        myController = (IController) registry.lookup("controller");
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




    private void displayError(Exception ex){
        //TODO: display graphical effor messagebox
        out.println("Error:"+ex.getMessage());
        Scanner input = new Scanner(System.in);

        out.println("Do you need stack trace? [y/n]");

        if(input.next().startsWith("y"))
            ex.printStackTrace();
    }
}

