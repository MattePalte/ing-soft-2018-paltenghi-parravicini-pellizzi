package project.ing.soft.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;

public class GuiBackgroundApp extends Application {

    private Stage primaryStage;
    private transient PrintStream out;


    @Override
    public void start(Stage aPrimaryStage) {
        this.primaryStage = aPrimaryStage;
        Parent root = null;
        String sceneFile = "/gui/layout/gui_view_layout.fxml";
        //String sceneFile = "/gui/layout/spalsh_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = (Parent)fxmlLoader.load();
            //root = FXMLLoader.load(getClass().getResource(sceneFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GuiView backBoneOfInterface = fxmlLoader.getController();
        out = new PrintStream(System.out);
        backBoneOfInterface.setOut(out);
        backBoneOfInterface.setPrimaryStage(aPrimaryStage);
        Scene scene = new Scene(root, 500, 600);
        /*SplashController splashFxController = fxmlLoader.getController();
        splashFxController.collectDimension(scene);*/

        this.primaryStage.setTitle("Sagrada - GUI");
        this.primaryStage.setScene(scene);

        this.primaryStage.setMaximized(true);
        this.primaryStage.show();



    }


}
