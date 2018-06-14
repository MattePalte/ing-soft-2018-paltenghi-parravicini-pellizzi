package project.ing.soft.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.ing.soft.Settings;
import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.APProxySocket;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.rmi.Naming;

public class SplashController {

    @FXML StackPane stackBox;
    @FXML ImageView ivSplash;

    private Stage stage;

    @FXML private Text msgLabel;
    @FXML private TextField txtName;
    @FXML private TextField txtToken;
    @FXML private TextField txtServerIP;
    @FXML private TextField txtServerPort;
    @FXML private Button btnConnect;
    @FXML private Button btnReconnect;
    @FXML private Pane content;
    @FXML private ToggleGroup connectionTypeGroup;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void connect(){
        String nick = txtName.getText();
        RadioButton selectedOption = (RadioButton) connectionTypeGroup.getSelectedToggle();
        String chosenType = selectedOption.getText();
        System.out.println("connect with -> " + selectedOption.getText());
        if (chosenType.toLowerCase().contains("rmi")) {
            connectRMI(nick);
        } else {
            connectSocket(nick);
        }
    }
    public void reconnect(){
        String nick = txtName.getText();
        String token = txtToken.getText();
        RadioButton selectedOption = (RadioButton) connectionTypeGroup.getSelectedToggle();
        String chosenType = selectedOption.getText();
        System.out.println("reconnect with -> " + selectedOption.getText());
        if (chosenType.toLowerCase().contains("rmi")) {
            reconnectRMI(nick, token);
        } else {
            reconnectSocket(nick, token);
        }
    }

    public void notifyConnectionEnstablished(String token){
        String nick = txtName.getText();
        Text msg = new Text("You are now connected with the nick: " + nick);
        Text tokenLbl = new Text("This is your token for reconnection: ");
        TextField txtToken = new TextField();
        txtToken.setText(token);
        txtToken.setEditable(false);
        content.setPadding(new Insets(50,50,100,50));
        content.getChildren().clear();
        content.getChildren().add(msg);
        content.getChildren().add(tokenLbl);
        content.getChildren().add(txtToken);
    }

    private int getPort(){
        String insertedPort = txtServerPort.getText();
        if (insertedPort.equals("")) return Settings.instance().getPort();
        int portNumber = Integer.parseInt(insertedPort);
        if (validPort(insertedPort)) {
            return portNumber;
        } else {
            return Settings.instance().getPort();
        }
    }

    private String getIP() {
        String insertedIP = txtServerIP.getText();
        if (insertedIP.equals("")) return Settings.instance().getHost();
        if (validIP(insertedIP)) {
            return insertedIP;
        } else {
            return Settings.instance().getHost();
        }
    }

    private boolean validIP(String ip) {
        try {
            String[] parts = ip.split( "\\." );
            if ( ip == null || ip.isEmpty() || parts.length != 4 || ip.endsWith(".")) {
                return false;
            }
            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    private boolean validPort(String port) {
        int portNumber = Integer.parseInt(port);
        return portNumber > 1024 && portNumber <= 49151;
    }

    private void connectRMI(String nick){
        IAccessPoint accessPoint = null;
        try {
            accessPoint = (IAccessPoint)Naming.lookup( Settings.instance().getRemoteRmiApName());

            System.out.println("1) AccessPoint reference obtained");
            IView realView = new RealView(stage, nick, this);
            System.out.println("2) view object created in BackGround");
            IController gameController = accessPoint.connect(nick, realView);
            realView.attachController(gameController);
            System.out.println("3) controller given to the view");
        } catch (NickNameAlreadyTakenException ex){
            System.out.println("x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("x) Probably the server is down (no remote object or no registry)");
            msgLabel.setText(ex.getMessage());
            return;
        }
    }

    private void connectSocket(String nick){
        IAccessPoint accessPoint = null;
        accessPoint = new APProxySocket(getIP(), getPort());
        System.out.println("1) AccessPoint Proxy created and connected");
        try {
            IView realView = new RealView(stage, nick, this);
            System.out.println("2) view object created in BackGround");
            IController gameController = accessPoint.connect(nick, realView);
            realView.attachController(gameController);
            System.out.println("3) controller given to the view");
        } catch (NickNameAlreadyTakenException ex){
            System.out.println("x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("x) Probably the server is down");
            msgLabel.setText(ex.getMessage());
            return;
        }
    }

    private void reconnectRMI(String nick, String token){
        IAccessPoint accessPoint = null;
        try {
            accessPoint = (IAccessPoint) Naming.lookup(Settings.instance().getRemoteRmiApName());

            System.out.println("1) AccessPoint reference obtained");
            IView realView = new RealView(stage, nick, this);
            System.out.println("2) view object created in BackGround");
            IController gameController = accessPoint.reconnect(nick, token, realView);
            realView.attachController(gameController);
            System.out.println("3) controller given to the view");
        } catch (ActionNotPermittedException | CodeInvalidException ex){
            System.out.println("x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("x) Probably the server is down (no remote object or no registry)");
            msgLabel.setText(ex.getMessage());
            return;
        }
    }

    private void reconnectSocket(String nick, String token){
        IAccessPoint accessPoint = null;
        accessPoint = new APProxySocket(getIP(), getPort());
        System.out.println("1) AccessPoint Proxy created and connected");
        try {
            IView realView = new RealView(stage, nick, this);
            System.out.println("2) view object created in BackGround");
            IController gameController = accessPoint.reconnect(nick, token, realView);
            realView.attachController(gameController);
            System.out.println("3) controller given to the view");
        } catch (ActionNotPermittedException | CodeInvalidException ex){
            System.out.println("x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("x) Probably the server is down");
            msgLabel.setText(ex.getMessage());
            return;
        }
    }


}

