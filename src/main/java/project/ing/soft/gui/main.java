package project.ing.soft.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.ing.soft.view.IView;

import java.io.IOException;

public class main extends Application{
    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        String sceneFile = "/rootUI.fxml";
        try {
            root = FXMLLoader.load(getClass().getResource(sceneFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 300, 275);

        primaryStage.setTitle("Sagrada - GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("va avanti");
    }
    public static void main(String[] args) {
        launch(args);
    }
}
