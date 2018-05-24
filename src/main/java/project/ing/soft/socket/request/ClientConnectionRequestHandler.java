package project.ing.soft.socket.request;

import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.socket.response.ConnectionEstabilishedResponse;
import project.ing.soft.socket.response.ConnectionRefusedResponse;
import project.ing.soft.socket.ViewProxyOverSocket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ClientConnectionRequestHandler implements Callable<Boolean>, ConnectionRequestHandler {
    private Socket clientSocket;
    private IAccessPoint accessPoint;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private IController controller;
    private String nickname;
    private ViewProxyOverSocket viewProxy;

    public ClientConnectionRequestHandler(Socket clientSocket, IAccessPoint accessPoint){
        this.clientSocket = clientSocket;
        this.accessPoint = accessPoint;
    }

    @Override
    public Boolean call() throws Exception{
        try {
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ConnectionRequest request = (ConnectionRequest)ois.readObject();
            request.accept(this);

        } catch (Exception e) {
            oos.writeObject(new ConnectionRefusedResponse(e));
        }
        return true;
    }

    @Override
    public void handle(JoinTheGameRequest request) throws Exception{
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois);
        controller = accessPoint.connect(request.getNickname(), viewProxy);
        nickname = request.getNickname();
        oos.writeObject(new ConnectionEstabilishedResponse());
        //TODO: connect must return a GameController instead of IController
        ((GameController)controller).joinTheGame(nickname, viewProxy);
    }

    @Override
    public void handle(ReconnectionRequest request) throws Exception{
        // TODO: implement method
    }

}
