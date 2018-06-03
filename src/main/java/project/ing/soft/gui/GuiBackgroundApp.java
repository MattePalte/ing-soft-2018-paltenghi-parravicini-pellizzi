package project.ing.soft.gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Main class of the GUI. It extends Application class of JavaFX
 */

public class GuiBackgroundApp extends Application {

    /**
     * Method that is automatically called during startup.
     * It loads the first scene containing the splashscreen and login.
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        // Load FXML
        Parent root = null;
        String sceneFile = "/gui/layout/spalsh_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println(" BackGroundApp -> Cause: "+e.getCause() + "\n Message " + e.getMessage());
            return;
        }
        // prepare transition
        FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
        // Change scene and pass stage to java fx controller behind the FXML
        Scene scene = new Scene(root);
        SplashController splashFxController = fxmlLoader.getController();
        splashFxController.setStage(stage);
        // Sett parameters of stage and show
        stage.setTitle("Sagrada - GUI");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Listener to handle closing of the window and all related thread
        stage.setOnCloseRequest(new EventHandler<>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


}
