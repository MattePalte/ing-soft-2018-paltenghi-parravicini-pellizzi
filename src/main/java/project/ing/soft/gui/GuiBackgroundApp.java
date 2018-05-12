package project.ing.soft.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.ing.soft.model.gamemanager.events.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;

public class GuiBackgroundApp extends Application {

    private Stage primaryStage;
    private transient PrintStream out;
    private transient Queue<Event> eventsReceived;


    @Override
    public void start(Stage aPrimaryStage) {
        this.primaryStage = aPrimaryStage;
        Parent root = null;
        String sceneFile = "/gui_view_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = (Parent)fxmlLoader.load();
            //root = FXMLLoader.load(getClass().getResource(sceneFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GuiView backBoneOfInterface = fxmlLoader.getController();
        out = new PrintStream(System.out);
        eventsReceived = new LinkedList<>();
        // event list is shared with guiView before starting the interface
        backBoneOfInterface.setEventsReceived(eventsReceived);
        backBoneOfInterface.setOut(out);
        backBoneOfInterface.setPrimaryStage(aPrimaryStage);
        Scene scene = new Scene(root, 500, 600);

        this.primaryStage.setTitle("Sagrada - GUI");
        this.primaryStage.setScene(scene);
        this.primaryStage.setMaximized(true);
        this.primaryStage.show();
    }


}
