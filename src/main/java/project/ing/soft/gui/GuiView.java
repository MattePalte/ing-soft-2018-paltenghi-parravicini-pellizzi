package project.ing.soft.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.ing.soft.Colour;
import project.ing.soft.Die;
import project.ing.soft.cards.Constraint;
import project.ing.soft.cards.WindowPattern;
import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.controller.IController;
import project.ing.soft.events.*;
import project.ing.soft.events.Event;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.view.IView;

import java.io.IOException;
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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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

    // FOR FURTHER DEVELOP
    @FXML
    private Button btnStartGame;

    public GuiView() throws RemoteException {
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public void setEventsReceived(Queue<Event> eventsReceived) {
        this.eventsReceived = eventsReceived;
    }

    public void setOwnerNameOfTheView(String ownerNameOfTheView) {
        this.ownerNameOfTheView = ownerNameOfTheView;
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
        Parent root = null;
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
        primaryStage.setScene(scene);

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
        displayPatternCard(possiblePatterns.get(currentIndexPatternDisplayed), scene);
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {

    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        localCopyOfTheStatus = event.getaGameCopy();
        out.println("gestione di Model changed");
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
        Map<Colour, String> map = new HashMap<>();
        map.put(Colour.BLUE, "#4286f4");
        map.put(Colour.VIOLET, "#b762fc");
        map.put(Colour.RED, "#fc5067");
        map.put(Colour.GREEN, "#6af278");
        map.put(Colour.YELLOW, "#f5f97a");
        map.put(Colour.WHITE, "#ffffff");
        for (int row = 0 ; row < pattern.getHeight(); row++) {
            for (int col = 0 ; col < pattern.getWidth(); col++) {

                Button currentCell = (Button) scene.lookup("#pos" + row + col);
                Constraint constraint = pattern.getConstraintsMatrix()[row][col];
                if (constraint.getValue() != 0) {
                    currentCell.setText(constraint.getXLMescapeEncoding());
                } else {
                    currentCell.setText(" ");
                    //currentCell.setText(new Die(5, Colour.BLUE).getXLMescapeEncoding());
                }
                currentCell.setStyle("-fx-background-color:" + map.get(constraint.getColour()) + "; -fx-font: 22 monospace;");
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
    public void handleException(Exception ex) throws IOException, Exception {

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
                myController.choosePattern(ownerNameOfTheView, c, true);
                return;
            }
            if (c.getRearPattern() == chosenPattern) {
                myController.choosePattern(ownerNameOfTheView, c, false);
                return;
            }
        }
        btnNext.setDisable(true);
        btnPrev.setDisable(true);
        return;
    }
    public void btnClickStart(ActionEvent actionEvent) throws Exception {
        /*Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Parent root = null;
        String sceneFile = "/gameUI.fxml";
        try {
            root = FXMLLoader.load(getClass().getResource(sceneFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 400, 400);

        primaryStage.setTitle("Sagrada - GUI");
        primaryStage.setScene(scene);*/

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




    private void displayError(Exception ex){
        //TODO: display graphical effor messagebox
        out.println("Error:"+ex.getMessage());
        Scanner input = new Scanner(System.in);

        out.println("Do you need stack trace? [y/n]");

        if(input.next().startsWith("y"))
            ex.printStackTrace();
    }
}

