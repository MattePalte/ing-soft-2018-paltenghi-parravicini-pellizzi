package project.ing.soft.socket.request.ConnectionRequest;

import org.apache.commons.codec.binary.Hex;
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
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private String computeDigest(String toCompute) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(toCompute.getBytes());
            byte[] digest = md.digest();
            return new String(Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("A problem occurred trying to compute hash function: ");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void handle(JoinTheGameRequest request) throws Exception{
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois);
        controller = accessPoint.connect(request.getNickname(), viewProxy);
        nickname = request.getNickname();
        String token = computeDigest(nickname + controller.getControllerSecurityCode());
        //TODO: delete this print on definitive version
        System.out.printf("Associated (%s, %s) token: %s\n", nickname, controller.getControllerSecurityCode(), token);
        oos.writeObject(new ConnectionEstabilishedResponse(token));
        //TODO: connect must return a GameController instead of IController
        ((GameController)controller).joinTheGame(nickname, viewProxy);
    }

    @Override
    public void handle(ReconnectionRequest request) throws Exception{
        // TODO: implement method
    }

}
