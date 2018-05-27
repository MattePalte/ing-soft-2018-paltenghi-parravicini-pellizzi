package project.ing.soft.accesspoint;

import project.ing.soft.controller.IController;
import project.ing.soft.socket.request.connectionrequest.ReconnectionRequest;
import project.ing.soft.socket.response.ConnectionResponse.*;
import project.ing.soft.socket.ControllerProxyOverSocket;
import project.ing.soft.socket.request.connectionrequest.JoinTheGameRequest;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Logger log;

    public APProxy(String host, int port){
        this.host = host;
        this.port = port;
        this.log  = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Level.OFF);
    }

    @Override
    public IController connect(String nickname, IView clientView) throws InterruptedException {
        log.log(Level.INFO,"{0} request to connect", nickname);
        mySocket = new Socket();
        Future ft;
        try {
            mySocket.connect(new InetSocketAddress(host, port));
            view = clientView;
            viewPrintStream = clientView.getPrintStream();
            oos = new ObjectOutputStream(mySocket.getOutputStream());
            ois = new ObjectInputStream(mySocket.getInputStream());
            ft = ex.submit(() -> {
                requestConnection(nickname);
                return true;
            });


            ft.get();
            return controllerProxy;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "error while connecting", e);
        }

        return null;
    }

    private void requestConnection(String nickname) throws IOException, ClassNotFoundException {
        log.log(Level.INFO,"{0} request to connect", nickname);
        oos.writeObject(new JoinTheGameRequest(nickname));
        ConnectionResponse response = (ConnectionResponse)ois.readObject();
        response.accept(this);

        controllerProxy = new ControllerProxyOverSocket(view, mySocket, oos, ois);
        view.attachController(controllerProxy);
        controllerProxy.start();

    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) {
        mySocket = new Socket();
        log.log(Level.INFO,"{0} requested to reconnect", nickname);
        try {
            mySocket.connect(new InetSocketAddress(host, port));
            view = clientView;
            viewPrintStream = clientView.getPrintStream();
            oos = new ObjectOutputStream(mySocket.getOutputStream());
            ois = new ObjectInputStream(mySocket.getInputStream());
            oos.writeObject(new ReconnectionRequest(nickname, code));
            ConnectionResponse response = (ConnectionResponse) ois.readObject();
            controllerProxy = new ControllerProxyOverSocket(view, mySocket, oos, ois);
            view.attachController(controllerProxy);
            controllerProxy.start();
            response.accept(this);
        } catch (Exception e) {
            log.log(Level.SEVERE, "error while connecting", e);
        }
        return controllerProxy;
    }

    @Override
    public void handle(ConnectionEstabilishedResponse response) {
        log.log(Level.INFO,"Connection request accepted");
        viewPrintStream.println("Connection established. Please, wait for the game to start");
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
            log.log(Level.SEVERE, "error while closing the socket", e);
        }

    }

    @Override
    public void handle(NickNameAlreadyTakenResponse response){
        viewPrintStream.println(response.getCause().getMessage());
        viewPrintStream.println("Please, select another nickname");
        Scanner in = new Scanner(System.in);
        String nickname = in.nextLine();
        System.out.println("Your nickname is " + nickname);
        try {
            mySocket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "error while closing the socket", e); e.printStackTrace();
        }
        controllerProxy.interrupt();
        ex.submit(() -> {
            connect(nickname, view);
            return true;
        });
    }

}
