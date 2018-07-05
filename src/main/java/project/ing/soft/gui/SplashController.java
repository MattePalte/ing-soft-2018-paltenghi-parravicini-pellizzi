package project.ing.soft.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SplashController {

    @FXML StackPane stackBox;
    @FXML ImageView ivSplash;

    private Stage stage;
    private  final Logger log;

    @FXML private Text msgLabel;
    @FXML private TextField txtName;
    @FXML private TextField txtToken;
    @FXML private TextField txtServerIP;
    @FXML private TextField txtServerPort;
    @FXML private Button btnConnect;
    @FXML private Button btnReconnect;
    @FXML private Pane content;
    @FXML private ToggleGroup connectionTypeGroup;

    private static final String INFO_1_ACCESS_POINT_RMI = "1) AccessPoint reference obtained";
    private static final String INFO_1_ACCESS_POINT_SOCKET = "1) AccessPoint Proxy created and connected";
    private static final String INFO_2_VIEW_CREATED = "2) view object created in BackGround";
    private static final String INFO_3_CONTROLLER_LINKED = "3) controller given to the view";
    private static final String INFO_SERVER_ERROR_RMI = "x) Probably the server is down (no remote object or no registry)";
    private static final String INFO_SERVER_ERROR_SOCKET = "x) Probably the server is down";

    public SplashController() {
        this.log = Logger.getLogger(Objects.toString(this));
    }

    @FXML
    public void initialize(){
        Preferences pref = Preferences.userRoot().node(Settings.instance().getProperty("preferences.location"));
        String token = pref.get(Settings.instance().getProperty("preferences.connection.token.location"), "");
        txtToken.setText(token);
    }

    /**
     * Saves the window of the GUI to be able to manipulate it, if needed
     * @param stage main window frame of the game
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Method to request connection to the server
     */
    public void connect(){
        String nick = txtName.getText();
        RadioButton selectedOption = (RadioButton) connectionTypeGroup.getSelectedToggle();
        String chosenType = selectedOption.getText();
        log.log(Level.INFO,"connect with -> {0}", selectedOption.getText());
        if (chosenType.toLowerCase().contains("rmi")) {
            connectRMI(nick);
        } else {
            connectSocket(nick);
        }
    }

    /**
     * Method to request reconnection to the server with a token
     */
    public void reconnect(){
        String nick = txtName.getText();
        String token = txtToken.getText();
        RadioButton selectedOption = (RadioButton) connectionTypeGroup.getSelectedToggle();
        String chosenType = selectedOption.getText();
        log.log(Level.INFO,"reconnect with -> {0}",  selectedOption.getText());
        if (chosenType.toLowerCase().contains("rmi")) {
            reconnectRMI(nick, token);
        } else {
            reconnectSocket(nick, token);
        }
    }

    /**
     * Method to print on the screen the message of successful connection
     * and the token retrieved by the server
     * @param token to use for reconnection
     */
    public void notifyConnectionEnstablished(String token){
        String nick = txtName.getText();
        Text msg = new Text("You are now connected with the nick: " + nick);
        Text tokenLbl = new Text("This is your token for reconnection: ");
        TextField txtWithNewToken = new TextField();
        txtWithNewToken.setText(token);
        txtWithNewToken.setEditable(false);
        content.setPadding(new Insets(50,50,100,50));
        content.getChildren().clear();
        content.getChildren().add(msg);
        content.getChildren().add(tokenLbl);
        content.getChildren().add(txtWithNewToken);

        Preferences pref = Preferences.userRoot().node(Settings.instance().getProperty("preferences.location"));
        pref.put(Settings.instance().getProperty("preferences.connection.token.location"), token);
        try {
            pref.flush();
        } catch (BackingStoreException e) {
            log.log(Level.INFO,"exception thrown while saving token" , e);
        }
    }

    /**
     * Method to get the port from the user interface
     * @return the port number as an integer
     */
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

    /**
     * Method to the ip number from the user interface
     * @return ip number in the right format as a String
     */
    private String getIP() {
        String insertedIP = txtServerIP.getText();
        if (insertedIP.equals("")) return Settings.instance().getHost();
        if (validIP(insertedIP)) {
            return insertedIP;
        } else {
            return Settings.instance().getHost();
        }
    }

    /**
     * Method to check that the given IP is valid
     * @param ip to check
     * @return true -> is valid or false -> invalid IP
     */
    private boolean validIP(String ip) {
        try {
            String[] parts = ip.split( "\\." );
            if ( ip.isEmpty() || parts.length != 4 || ip.endsWith(".")) {
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

    /**
     * Method to check that the given port is valid
     * @param port to check
     * @return true -> is valid or false -> invalid port number
     */
    private boolean validPort(String port) {
        int portNumber = Integer.parseInt(port);
        return portNumber > 1024 && portNumber <= 49151;
    }

    /**
     * Connect to the server via RMI
     * @param nick nickname of the current client
     */
    private void connectRMI(String nick){
        IAccessPoint accessPoint = null;
        try {
            accessPoint = (IAccessPoint)Naming.lookup( Settings.instance().getRemoteRmiApName(getIP()));

            log.log(Level.INFO, INFO_1_ACCESS_POINT_RMI);
            IView realView = new RealView(stage, nick, this);
            log.log(Level.INFO, INFO_2_VIEW_CREATED);
            IController gameController = accessPoint.connect(nick, realView);
            realView.attachController(gameController);
            log.log(Level.INFO, INFO_3_CONTROLLER_LINKED);
        } catch (NickNameAlreadyTakenException ex){
            log.log(Level.INFO,"x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            log.log(Level.INFO, INFO_SERVER_ERROR_RMI);
            msgLabel.setText(ex.getMessage());
        }
    }
    /**
     * Connect to the server via Socket
     * @param nick nickname of the current client
     */
    private void connectSocket(String nick){
        IAccessPoint accessPoint = null;

        try {
            accessPoint = new APProxySocket(getIP(), getPort());
            log.log(Level.INFO, INFO_1_ACCESS_POINT_SOCKET);
            IView realView = new RealView(stage, nick, this);
            log.log(Level.INFO, INFO_2_VIEW_CREATED);
            IController gameController = accessPoint.connect(nick, realView);
            realView.attachController(gameController);
            log.log(Level.INFO, INFO_3_CONTROLLER_LINKED);
        } catch (NickNameAlreadyTakenException ex){
            log.log(Level.INFO,"x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            log.log(Level.INFO, INFO_SERVER_ERROR_SOCKET);
            msgLabel.setText(ex.getMessage());
        }
    }
    /**
     * Reconnect to the server via RMI
     * @param nick nickname of the current client
     * @param token code to reconnect
     */
    private void reconnectRMI(String nick, String token){
        IAccessPoint accessPoint = null;
        try {
            accessPoint = (IAccessPoint) Naming.lookup(Settings.instance().getRemoteRmiApName(getIP()));

            log.log(Level.INFO, INFO_1_ACCESS_POINT_RMI);
            IView realView = new RealView(stage, nick, this);
            log.log(Level.INFO, INFO_2_VIEW_CREATED);
            IController gameController = accessPoint.reconnect(nick, token, realView);
            realView.attachController(gameController);
            log.log(Level.INFO, INFO_3_CONTROLLER_LINKED);
        } catch (ActionNotPermittedException | CodeInvalidException ex){
            log.log(Level.INFO,"x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            log.log(Level.INFO, INFO_SERVER_ERROR_RMI);
            msgLabel.setText(ex.getMessage());
        }
    }
    /**
     * Reconnect to the server via Socket
     * @param nick nickname of the current client
     * @param token code to reconnect
     */
    private void reconnectSocket(String nick, String token){
        IAccessPoint accessPoint = null;

        try {
            accessPoint = new APProxySocket(getIP(), getPort());
            log.log(Level.INFO, INFO_1_ACCESS_POINT_SOCKET);
            IView realView = new RealView(stage, nick, this);
            log.log(Level.INFO, INFO_2_VIEW_CREATED);
            IController gameController = accessPoint.reconnect(nick, token, realView);
            realView.attachController(gameController);
            log.log(Level.INFO, INFO_3_CONTROLLER_LINKED);
        } catch (ActionNotPermittedException | CodeInvalidException ex){
            log.log(Level.INFO,"x) " + ex.getMessage());
            msgLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            log.log(Level.INFO, INFO_SERVER_ERROR_SOCKET);
            msgLabel.setText(ex.getMessage());
        }
    }


}

