package project.ing.soft.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;
import project.ing.soft.view.LocalViewCli;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RootUIController {
    @FXML
    private Button button;
    @FXML
    private Button btnConnect;
    @FXML
    private TextField txtName;
    @FXML
    private Button btnStartGame;

    private IController myController;
    private IView myView;

    public void btnClickJoin(ActionEvent actionEvent) throws Exception {
        System.out.println("Sto per accedere al match con nome " + txtName.getText());
        myController.joinTheGame(txtName.getText(), myView);

    }
    public void btnClickStart(ActionEvent actionEvent) throws Exception {
        Stage primaryStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Parent root = null;
        String sceneFile = "/gameUI.fxml";
        try {
            root = FXMLLoader.load(getClass().getResource(sceneFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 400, 400);

        primaryStage.setTitle("Sagrada - GUI");
        primaryStage.setScene(scene);

    }

    public void btnClickConnect(ActionEvent actionEvent) throws Exception {
        Registry registry = LocateRegistry.getRegistry();

        // gets a reference for the remote controller
        myController = (IController) registry.lookup("controller");
        System.out.println("Name inserted: " + txtName.getText());
        // creates and launches the view
        myView = new LocalViewCli(txtName.getText());

        myView.attachController(myController);
        myView.run();
        System.out.println("View was created!");
        btnConnect.setText("Connesso");
        btnConnect.setDisable(true);


    }
}

