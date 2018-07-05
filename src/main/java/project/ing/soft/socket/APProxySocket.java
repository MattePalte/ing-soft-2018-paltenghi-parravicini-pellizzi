package project.ing.soft.socket;

import project.ing.soft.Settings;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.ConnectionRefusedException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.request.connectionrequest.APReconnectRequest;
import project.ing.soft.socket.response.connectionresponse.*;
import project.ing.soft.socket.request.connectionrequest.APConnectRequest;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A specific-technology implementation of {@link project.ing.soft.accesspoint.IAccessPoint} in
 * socket context context that acts at the client side. It's counterparts it's represented by {@link APointSocket}
 * A template design pattern was taken into account to realize this class but after considering the fact that
 * in socket context would introduce a lot overhead even for not-already-binded player, we decided to design it
 * using a decorator fashion.
 * The APProxySocket permit socket users to access  {@link project.ing.soft.accesspoint.AccessPointReal}
 * which is in charge of admitting and discharging player connection request.
 * If a request is not satisfied it could be reused
 */
public class APProxySocket implements IAccessPoint, ConnectionResponseHandler {

    private Socket mySocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private final Logger log;

    public APProxySocket(String host, int port) throws IOException {
        this.log  = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Settings.instance().getDefaultLoggingLevel());

        mySocket = new Socket();
        try {
            mySocket.connect(new InetSocketAddress(host, port));
            oos = new ObjectOutputStream(mySocket.getOutputStream());
            ois = new ObjectInputStream(mySocket.getInputStream());
        } catch(IOException e){
            log.log(Level.SEVERE, "error while connecting", e);
            throw e;
        }
    }


    @Override
    public synchronized IController connect(String nickname, IView clientView) throws Exception {
        ControllerProxyOverSocket controllerProxy;
        log.log(Level.INFO,"{0} request to connect", nickname);
        oos.writeObject(new APConnectRequest(nickname));
        ConnectionResponse response = (ConnectionResponse)ois.readObject();
        response.accept(this);

        controllerProxy = new ControllerProxyOverSocket(clientView, mySocket, oos, ois);
        clientView.attachController(controllerProxy);
        controllerProxy.start();
        //in order to avoid request superposition
        mySocket = null;
        ois = null;
        oos = null;
        return controllerProxy;
    }

    @Override
    public synchronized IController reconnect(String nickname, String code, IView clientView) throws Exception {
        log.log(Level.INFO,"{0} requested to reconnectPlayer", nickname);

        oos.writeObject(new APReconnectRequest(nickname, code));
        ConnectionResponse response = (ConnectionResponse) ois.readObject();
        response.accept(this);
        ControllerProxyOverSocket controllerProxy = new ControllerProxyOverSocket(clientView, mySocket, oos, ois);
        clientView.attachController(controllerProxy);
        controllerProxy.start();
        //in order to avoid request superposition
        mySocket = null;
        ois = null;
        oos = null;
        return controllerProxy;
    }

    @Override
    public void handle(ConnectionEstabilishedResponse response) {
        log.log(Level.INFO,"Connection request has been accepted");
    }

    @Override
    public void handle(ConnectionRefusedResponse response) throws Exception{
        throw new ConnectionRefusedException(response.getCause());
    }

    @Override
    public void handle(NickNameAlreadyTakenResponse response) throws Exception{
        throw new NickNameAlreadyTakenException(response.getCause().getMessage());
    }

}
