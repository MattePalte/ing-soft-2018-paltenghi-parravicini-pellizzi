package project.ing.soft.gui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.ing.soft.controller.IController;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.gamemodel.events.*;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Class that emulates is the view Client-frontSide.
 *  It receives updates from the server through an MVC pattern with events.
 *  It also handles those events by changing scene of the stage for different phases of the game.
 */

public class RealView extends UnicastRemoteObject implements IView, IEventHandler, Serializable{
    private IGameModel localCopyOfTheStatus;
    private Player myPlayer;
    private final transient String ownerNameOfTheView;
    private transient IController myController;
    private transient String token;
    private boolean stopResponding = false;
    private transient MainLayoutController mainBoard;
    private transient SplashController splashController;
    private final transient Queue<Event> eventsReceived = new LinkedList<>();
    private final transient Stage stage;
    private final transient Logger log;


    public RealView(Stage stage, String nick, SplashController splashController) throws RemoteException{
        super();
        this.stage = stage;
        this.ownerNameOfTheView = nick;
        this.splashController = splashController;
        this.log = Logger.getLogger(Objects.toString(this));
        startTaskForEventsListening();
    }

    /**
     * Method that starts a task in the background. This task constantly checks the
     * queue of events. When an event is present, it is passed to a Runnable that
     * executes it on and eventhandler (in this case RealView itself.
     */
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
                            Thread.currentThread().interrupt();
                        }
                    }
                    if (toRespond != null) {
                        Platform.runLater(new EventRunnable(toRespond, eventHandler));
                        log.log(Level.INFO,"BG view is handling event: {0}", toRespond);
                    }
                    toRespond = null;
                    synchronized (eventsReceived) {
                        eventsReceived.notifyAll();
                    }
                }
                return null;
            }
        };
        // start task and set as daemon
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }


    @Override
    public void respondTo(CurrentPlayerChangedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
        }
    }

    @Override
    public void respondTo(FinishedSetupEvent event) {
        Parent root = null;
        String sceneFile = "/gui/layout/main_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            printExceptionMsgAndCause(e);
            return;
        }

        Scene scene = new Scene(root);
        MainLayoutController mainLayoutController = fxmlLoader.getController();
        mainLayoutController.setOwnerNameOfTheView(ownerNameOfTheView);
        mainLayoutController.setStage(stage);
        mainLayoutController.setToken(token);
        mainLayoutController.setGameController(myController);
        mainLayoutController.setLocalCopyOfTheStatus(localCopyOfTheStatus);
        mainBoard = mainLayoutController;
        stage.setTitle("Main Board");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(true);
        // handle event
        mainBoard.respondTo(event);
        // show mainBoard
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);

        stage.show();
    }

    @Override
    public void respondTo(GameFinishedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
        }
        stopResponding = true;
    }

    @Override
    public void respondTo(PatternCardDistributedEvent event) {
        Parent root = null;
        String sceneFile = "/gui/layout/choose_pattern_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            printExceptionMsgAndCause(e);
            return;
        }
        // prepare transition
        FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        // Show scene
        Scene scene = new Scene(root);
        ChoosePatternController choosePatternController = fxmlLoader.getController();
        choosePatternController.setNick(ownerNameOfTheView);
        choosePatternController.setStage(stage);
        choosePatternController.setPrivObj(myPlayer.getPrivateObjective());
        choosePatternController.setPatternEvent(event);
        choosePatternController.setGameController(myController);
        choosePatternController.renderThings();
        // Prepare stage
        stage.hide();
        stage.setResizable(true);
        stage.setTitle("Choose Pattern Card");
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.sizeToScene();
        ft.play();
        stage.show();
    }

    @Override
    public void respondTo(MyTurnStartedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
        }
    }

    @Override
    public void respondTo(ModelChangedEvent event) {
        this.localCopyOfTheStatus = event.getaGameCopy();
        for (Player p : localCopyOfTheStatus.getPlayerList()){
            if (p.getName().equals(ownerNameOfTheView)) {
                this.myPlayer = p;
                break;
            }
        }
        // normal forwarding scenario
        if (mainBoard != null && myPlayer.getPattern() != null) {
            mainBoard.respondTo(event);
        }
        // reconnection scenario
        if (mainBoard == null && myPlayer.getPattern() != null) {
            createBoardAfterReconnection(event);
        }
    }

    @Override
    public void respondTo(MyTurnEndedEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
        }
    }

    @Override
    public void respondTo(ToolcardActionRequestEvent event) {
        if (mainBoard != null) {
            mainBoard.respondTo(event);
        }
    }

    @Override
    public void respondTo(SetTokenEvent event) {
        this.token = event.getToken();
        log.log(Level.INFO,"TOKEN -> " + event.getToken());
        splashController.notifyConnectionEnstablished(event.getToken());
    }

    private void createBoardAfterReconnection(ModelChangedEvent event){
        Parent root = null;
        String sceneFile = "/gui/layout/main_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            log.log(Level.INFO,"Cause: "+e.getCause() + "\n Message " + e.getMessage());
        }
        Scene scene = new Scene(root);
        MainLayoutController mainLayoutController = fxmlLoader.getController();
        mainLayoutController.setOwnerNameOfTheView(ownerNameOfTheView);
        mainLayoutController.setStage(stage);
        mainLayoutController.setToken(token);
        mainLayoutController.setGameController(myController);
        mainLayoutController.setLocalCopyOfTheStatus(localCopyOfTheStatus);
        for (Player p : localCopyOfTheStatus.getPlayerList()){
            if (p.getName().equals(ownerNameOfTheView)) {
                this.myPlayer = p;
                break;
            }
        }
        mainBoard = mainLayoutController;
        stage.setTitle("Main Board");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(true);
        // handle model changed event
        mainBoard.respondTo(event);
        // simulate a finished setup to draw public cards and token
        mainBoard.respondTo(new FinishedSetupEvent());
        if (myPlayer.getName().equals(localCopyOfTheStatus.getCurrentPlayer().getName())) {
            mainBoard.respondTo(new MyTurnStartedEvent());
        }
        // show mainBoard
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
    }


    @Override
    public void update(Event event) throws IOException {
        log.log(Level.INFO,getClass().getName() + " received an event :" + event);

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
        // useless method
    }


    private void displayError(Exception ex){
        String stack = Arrays.toString(ex.getStackTrace());
        log.log(Level.INFO, stack);
        Platform.runLater(
                () -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exeption");
                    alert.setHeaderText("Information Alert");
                    alert.setContentText(ex.getMessage());
                    alert.show();
                }
        );
    }

    private void printExceptionMsgAndCause(Exception e ){
        log.log(Level.INFO,"Cause: "+e.getCause() + "\n Message " + e.getMessage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RealView realView = (RealView) o;

        return  !(
                (stopResponding != realView.stopResponding) ||
                (localCopyOfTheStatus != null ? !localCopyOfTheStatus.equals(realView.localCopyOfTheStatus) : realView.localCopyOfTheStatus != null) ||
                (myPlayer != null ? !myPlayer.equals(realView.myPlayer) : realView.myPlayer != null) ||
                (ownerNameOfTheView != null ? !ownerNameOfTheView.equals(realView.ownerNameOfTheView) : realView.ownerNameOfTheView != null) ||
                (myController != null ? !myController.equals(realView.myController) : realView.myController != null) ||
                (token != null ? !token.equals(realView.token) : realView.token != null) ||
                (mainBoard != null ? !mainBoard.equals(realView.mainBoard) : realView.mainBoard != null) ||
                (!eventsReceived.equals(realView.eventsReceived)) ||
                (stage != null ? !stage.equals(realView.stage) : realView.stage != null) ||
                !(log != null ? log.equals(realView.log) : realView.log == null)
            );
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (localCopyOfTheStatus != null ? localCopyOfTheStatus.hashCode() : 0);
        result = 31 * result + (myPlayer != null ? myPlayer.hashCode() : 0);
        result = 31 * result + (ownerNameOfTheView != null ? ownerNameOfTheView.hashCode() : 0);
        result = 31 * result + (myController != null ? myController.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (stopResponding ? 1 : 0);
        result = 31 * result + (mainBoard != null ? mainBoard.hashCode() : 0);
        result = 31 * result + eventsReceived.hashCode();
        result = 31 * result + (stage != null ? stage.hashCode() : 0);
        result = 31 * result + (log != null ? log.hashCode() : 0);
        return result;
    }
}
