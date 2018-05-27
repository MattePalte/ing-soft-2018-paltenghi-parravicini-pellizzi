package project.ing.soft.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.ing.soft.Settings;

import java.io.IOException;
import java.io.PrintStream;

public class GuiBackgroundApp extends Application {

    @Override
    public void start(Stage stage) {
        Parent root = null;
        //String sceneFile = "/gui/layout/gui_view_layout.fxml";
        String sceneFile = "/gui/layout/spalsh_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = (Parent)fxmlLoader.load();
            //root = FXMLLoader.load(getClass().getResource(sceneFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*GuiView viewFxController = fxmlLoader.getController();
        PrintStream out = new PrintStream(System.out);
        viewFxController.setOut(out);
        viewFxController.setStage(stage);*/
        Scene scene = new Scene(root);
        SplashController splashFxController = fxmlLoader.getController();
        splashFxController.collectDimension(scene);
        splashFxController.setStage(stage);

        stage.setTitle("Sagrada - GUI");
        stage.setScene(scene);
        stage.setResizable(false);

        //stage.setMaximized(true);
        stage.show();
    }


}
