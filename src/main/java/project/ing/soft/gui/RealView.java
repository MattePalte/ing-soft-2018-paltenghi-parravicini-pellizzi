package project.ing.soft.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;
import project.ing.soft.controller.IController;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.*;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

public class RealView extends UnicastRemoteObject implements IView, IEventHandler, Serializable{
    private IGameManager localCopyOfTheStatus;
    private Player myPlayer;
    private String ownerNameOfTheView;
    private transient IController myController;
    private transient String token;
    private boolean stopResponding = false;
    private MainLayoutController mainBoard;
    private final transient Queue<Event> eventsReceived = new LinkedList<>();
    private final Stage stage;

    public RealView(Stage stage, String nick) throws RemoteException {
        this.stage = stage;
        this.ownerNameOfTheView = nick;
        startTaskForEventsListening();
    }

    private void startTaskForEventsListening(){
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
                        System.out.println("BG view is handling event: " + toRespond.toString());
                    }
                    toRespond = null;
                    synchronized (eventsReceived) {
                        eventsReceived.notifyAll();
                    }
                }
                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }


    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
            return;
        }
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
            return;
        }
        Parent root = null;
        String sceneFile = "/gui/layout/main_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = (Parent)fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        MainLayoutController mainLayoutController = fxmlLoader.getController();
        mainLayoutController.setOut(System.out);
        mainLayoutController.setOwnerNameOfTheView(ownerNameOfTheView);
        mainLayoutController.setStage(stage);
        mainLayoutController.setGameController(myController);
        mainBoard = mainLayoutController;
        stage.setTitle("Main Board");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
            return;
        }
    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        Parent root = null;
        String sceneFile = "/gui/layout/choose_pattern_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = (Parent)fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root);
        ChoosePatternController choosePatternController = fxmlLoader.getController();
        choosePatternController.setNick(ownerNameOfTheView);
        choosePatternController.setStage(stage);
        choosePatternController.setPrivObj(myPlayer.getPrivateObjective());
        choosePatternController.setPatternEvent(event);
        choosePatternController.setGameController(myController);
        choosePatternController.renderThings();

        stage.setTitle("Choose Pattern Card");
        stage.setScene(scene);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
            return;
        }
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
            return;
        }
        this.localCopyOfTheStatus = event.getaGameCopy();
        for (Player p : localCopyOfTheStatus.getPlayerList()){
            if (p.getName().equals(ownerNameOfTheView)) {
                this.myPlayer = p;
                break;
            }
        }
    }

    @Override
    public void respondTo(MyTurnEndedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
            return;
        }
    }

    @Override
    public void respondTo(ToolcardActionRequestEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
            return;
        }
    }

    @Override
    public void respondTo(SetTokenEvent event) {
        this.token = event.getToken();
    }

    @Override
    public void update(Event event) throws IOException {
        System.out.println( " - " + ownerNameOfTheView + " ha ricevuto un evento :" + event);

        if (!stopResponding) {
            synchronized (eventsReceived) {
                eventsReceived.add(event);
                eventsReceived.notifyAll();
            }
        }
    }

    @Override
    public void attachController(IController gameController) throws IOException {
        this.myController = gameController;
    }

    @Override
    public void run() throws IOException {

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
