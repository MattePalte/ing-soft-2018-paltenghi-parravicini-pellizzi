package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.TokenCalculator;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.response.ConnectionResponse.ConnectionEstabilishedResponse;
import project.ing.soft.socket.response.ConnectionResponse.ConnectionRefusedResponse;
import project.ing.soft.socket.ViewProxyOverSocket;
import project.ing.soft.socket.response.ConnectionResponse.NickNameAlreadyTakenResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

public class ClientConnectionRequestHandler implements Callable<Boolean>, ConnectionRequestHandler {
    private Socket clientSocket;
    private IAccessPoint accessPoint;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
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

        } catch(NickNameAlreadyTakenException e){
            oos.writeObject(new NickNameAlreadyTakenResponse(e));
            clientSocket.close();
            viewProxy.interrupt();
        } catch (Exception e) {
            oos.writeObject(new ConnectionRefusedResponse(e));
            clientSocket.close();
            viewProxy.interrupt();
        }
        return true;
    }

    @Override
    public void handle(JoinTheGameRequest request) throws Exception{
        IController controller;
        String nickname;

        nickname = request.getNickname();
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois, nickname);
        controller = accessPoint.connect(nickname, viewProxy);
        String token = TokenCalculator.computeDigest(nickname + controller.getControllerSecurityCode());
        //TODO: delete this print on definitive version
        System.out.printf("Associated (%s, %s) token: %s%n", nickname, controller.getControllerSecurityCode(), token);
        oos.writeObject(new ConnectionEstabilishedResponse(token));
        //TODO: connect must return a GameController instead of IController
        ((GameController)controller).joinTheGame(nickname, viewProxy);
    }

    @Override
    public void handle(ReconnectionRequest request) throws Exception{
        // TODO: implement method
        IController controller;
        String nickname;
        String code;

        nickname = request.getNickname();
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois, nickname);
        code = request.getGameToken();
        controller = accessPoint.reconnect(nickname, code, viewProxy);
        oos.writeObject(new ConnectionEstabilishedResponse(code));
        ((GameController) controller).joinTheGame(nickname, viewProxy);
    }


}
