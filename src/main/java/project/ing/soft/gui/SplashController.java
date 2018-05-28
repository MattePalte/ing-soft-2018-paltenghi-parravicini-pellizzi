package project.ing.soft.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import project.ing.soft.Settings;
import project.ing.soft.accesspoint.APProxySocket;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SplashController {

    @FXML StackPane stackBox;
    @FXML ImageView ivSplash;

    /*
        private AtomicReference<Double> currentHeight = new AtomicReference<>();
        private AtomicReference<Double> currentWidth = new AtomicReference<>();
        private final double startWidth = 500;
        private final double startHeight = 422;
    */
    private Stage stage;

    @FXML
    private Button btnConnectRMI;
    @FXML
    private Button btnConnectSocket;
    @FXML
    private TextField txtName;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    protected void initialize(){
        /*Image img = new Image("gui/sagrada_small_splash.png");
        ivSplash.setImage(img);
        ivSplash.setFitWidth(startWidth);
        ivSplash.setFitHeight(startHeight);*/
    }

    public void collectDimension(Scene scene){
        /*scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                currentWidth.set(newSceneWidth.doubleValue());
                updateDimension(scene);

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                currentHeight.set(newSceneHeight.doubleValue());
                updateDimension(scene);
            }
        });*/
    }

    private synchronized void updateDimension(Scene scene){
        /*Stage stage = (Stage) scene.getWindow();
        if (currentWidth.get() == null || currentHeight.get() == null) return;
        Double curWidth = currentWidth.get();
        Double curHeight = currentHeight.get();
        Double ratio = startWidth/startHeight;
        Double widthImposedByHeight = curHeight*ratio;
        Double heightImposedBywidth = curWidth/ratio;
        //which is the constrain? height or width?
        Double desiredWidth = Math.min(widthImposedByHeight, curWidth);
        Double desiredHeight = Math.min(heightImposedBywidth, curHeight);
        ivSplash.setFitWidth(desiredWidth);
        ivSplash.setFitHeight(desiredHeight);*/
    }

    public void connectRMI(){
        IAccessPoint accessPoint = null;
        String nick = txtName.getText();
        try {
            Registry registry = LocateRegistry.getRegistry( Settings.instance().getDefaultIpForRMI());
            System.out.println("Objects currently registered in the registry");
            String[] registryList = registry.list();
            for(String s : registryList)
                System.out.println(s);
            accessPoint = (IAccessPoint) registry.lookup("accesspoint");
            System.out.println("1) AccessPoint reference obtained");
        } catch (Exception ex) {
            System.out.println("x) Probably the server is down (no remote object or no registry)");
            return;
        }
        Scene scene = createGameView(nick, accessPoint);
        changeScene(scene);
    }

    public void connectSocket(){
        IAccessPoint accessPoint = null;
        String nick = txtName.getText();
        accessPoint = new APProxySocket(Settings.instance().getHost(), Settings.instance().getPort());
        System.out.println("1) AccessPoint Proxy created and connected");
        Scene scene = createGameView(nick, accessPoint);
        changeScene(scene);
    }

    private Scene createGameView(String nick, IAccessPoint accessPoint){
        Parent root = null;
        String sceneFile = "/gui/layout/gui_view_layout.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(sceneFile));
        try {
            root = (Parent)fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problem loading the fxml");
            return null;
        }
        GuiView viewFxController = fxmlLoader.getController();
        PrintStream out = new PrintStream(System.out);
        viewFxController.setOut(out);
        viewFxController.setStage(stage);
        viewFxController.setOwnerNameOfTheView(nick);
        try {
            IController controllerFromRMI = (IController) accessPoint.connect(nick, viewFxController);
            System.out.println("2) Controller retrieved from AccessPoint gameID=" + controllerFromRMI.getControllerSecurityCode());
            viewFxController.attachController(controllerFromRMI);
            System.out.println("3) Controller attached to the view");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*FadeTransition ft = new FadeTransition(Duration.millis(3000), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();*/
        return new Scene(root);
    }

    private void changeScene(Scene scene) {
        stage.setScene(scene);
        System.out.println("4) Scene created and started");
        stage.setResizable(true);
        //Center newly created scene
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        //stage.setMaximized(true);
    }

}

