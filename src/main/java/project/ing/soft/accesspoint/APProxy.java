package project.ing.soft.accesspoint;

import project.ing.soft.controller.IController;
import project.ing.soft.socket.response.ConnectionResponse.*;
import project.ing.soft.socket.ControllerProxyOverSocket;
import project.ing.soft.socket.request.connectionrequest.JoinTheGameRequest;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APProxy implements IAccessPoint, ConnectionResponseHandler {

    private String host;
    private int port;
    private Socket mySocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ControllerProxyOverSocket controllerProxy;
    private IView view;
    private PrintStream viewPrintStream;
    private ExecutorService ex = Executors.newFixedThreadPool(1);

    public APProxy(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public IController connect(String nickname, IView clientView) throws RemoteException {
        mySocket = new Socket();

        try {
            mySocket.connect(new InetSocketAddress(host, port));
            view = clientView;
            viewPrintStream = clientView.getPrintStream();
            oos = new ObjectOutputStream(mySocket.getOutputStream());
            ois = new ObjectInputStream(mySocket.getInputStream());
            ex.submit(() -> {
                requestConnection(nickname);
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return controllerProxy;
    }

    private void requestConnection(String nickname) throws Exception{
        oos.writeObject(new JoinTheGameRequest(nickname));
        ConnectionResponse response = (ConnectionResponse)ois.readObject();
        controllerProxy = new ControllerProxyOverSocket(view, mySocket, oos, ois);
        view.attachController(controllerProxy);
        controllerProxy.start();
        response.accept(this);
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws RemoteException {
        return null;
    }

    @Override
    public void handle(ConnectionEstabilishedResponse response) {
        viewPrintStream.println("Connection estabilished. Please, wait for the game to start");
        viewPrintStream.println("Please remember to save this code to let you ask for reconnection in case of network problems");
        viewPrintStream.println("YOUR TOKEN TO ASK RECONNECTION IS: " + response.getToken());
    }

    @Override
    public void handle(ConnectionRefusedResponse response) {
        viewPrintStream.println("Connection refused: " + response.getCause());
        controllerProxy.interrupt();
        try {
            mySocket.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void handle(NickNameAlreadyTakenResponse response){
        viewPrintStream.println(response.getCause().getMessage());
        viewPrintStream.println("Please, select another nickname");
        Scanner in = new Scanner(System.in);
        String nickname = in.nextLine();
        System.out.println("Your nickname is " + nickname);
        ex.submit(() -> {
            connect(nickname, view);
            return true;
        });
        //controllerProxy.interrupt();
    }

}
