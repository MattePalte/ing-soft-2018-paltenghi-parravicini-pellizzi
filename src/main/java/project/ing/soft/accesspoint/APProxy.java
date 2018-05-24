package project.ing.soft.accesspoint;

import project.ing.soft.controller.IController;
import project.ing.soft.socket.response.ConnectionEstabilishedResponse;
import project.ing.soft.socket.response.ConnectionRefusedResponse;
import project.ing.soft.socket.response.ConnectionResponseHandler;
import project.ing.soft.socket.ControllerProxyOverSocket;
import project.ing.soft.socket.request.JoinTheGameRequest;
import project.ing.soft.socket.response.ConnectionResponse;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.RemoteException;

public class APProxy implements IAccessPoint, ConnectionResponseHandler {

    private String host;
    private int port;
    private ControllerProxyOverSocket controllerProxy;
    private PrintStream viewPrintStream;

    public APProxy(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public IController connect(String nickname, IView clientView) throws RemoteException {
        Socket aSocket = new Socket();
        ObjectOutputStream oos;
        ObjectInputStream ois;

        try {
            aSocket.connect(new InetSocketAddress(host, port));
            viewPrintStream = clientView.getPrintStream();
            oos = new ObjectOutputStream(aSocket.getOutputStream());
            ois = new ObjectInputStream(aSocket.getInputStream());
            oos.writeObject(new JoinTheGameRequest(nickname));
            ConnectionResponse response = (ConnectionResponse)ois.readObject();
            controllerProxy = new ControllerProxyOverSocket(clientView, aSocket, oos, ois);
            clientView.attachController(controllerProxy);
            controllerProxy.start();
            response.accept(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return controllerProxy;
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws RemoteException {
        return null;
    }

    @Override
    public void handle(ConnectionEstabilishedResponse response) {
        viewPrintStream.println("Connection estabilished. Please, wait for the game to start");
    }

    @Override
    public void handle(ConnectionRefusedResponse response) {
        viewPrintStream.println("Connection refused: ");
        viewPrintStream.println(response.getCause().getMessage());
    }

}
