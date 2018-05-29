package project.ing.soft.accesspoint;

import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.ConnectionRefusedException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.model.gamemanager.events.SetTokenEvent;
import project.ing.soft.socket.request.connectionrequest.APReconnectRequest;
import project.ing.soft.socket.response.connectionresponse.*;
import project.ing.soft.socket.ControllerProxyOverSocket;
import project.ing.soft.socket.request.connectionrequest.APConnectRequest;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class APProxySocket implements IAccessPoint, ConnectionResponseHandler {

    private Socket mySocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private IView view;
    private Logger log;

    public APProxySocket(String host, int port){
        this.log  = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Level.OFF);

        mySocket = new Socket();
        try {
            mySocket.connect(new InetSocketAddress(host, port));
            oos = new ObjectOutputStream(mySocket.getOutputStream());
            ois = new ObjectInputStream(mySocket.getInputStream());
        } catch(IOException e){
            log.log(Level.SEVERE, "error while connecting", e);

        }
    }

    @Override
    public IController connect(String nickname, IView clientView) throws Exception {
        ControllerProxyOverSocket controllerProxy;

        log.log(Level.INFO,"{0} request to connect", nickname);
        oos.writeObject(new APConnectRequest(nickname));
        ConnectionResponse response = (ConnectionResponse)ois.readObject();
        view = clientView;
        response.accept(this);

        controllerProxy = new ControllerProxyOverSocket(clientView, mySocket, oos, ois);
        clientView.attachController(controllerProxy);
        controllerProxy.start();
        return controllerProxy;
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws Exception {
        ControllerProxyOverSocket controllerProxy = null;
        log.log(Level.INFO,"{0} requested to reconnect", nickname);
        try {
            oos.writeObject(new APReconnectRequest(nickname, code));
            ConnectionResponse response = (ConnectionResponse) ois.readObject();
            view = clientView;
            response.accept(this);
            controllerProxy = new ControllerProxyOverSocket(clientView, mySocket, oos, ois);
            clientView.attachController(controllerProxy);
            controllerProxy.start();
        } catch (IOException | ClassNotFoundException e) {
            log.log(Level.SEVERE, "error while connecting", e);
        }
        return controllerProxy;
    }

    @Override
    public void handle(ConnectionEstabilishedResponse response) throws IOException {
        log.log(Level.INFO,"Connection request accepted");
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
