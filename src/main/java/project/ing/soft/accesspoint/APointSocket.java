package project.ing.soft.accesspoint;

import project.ing.soft.accesspoint.AccessPointReal;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.request.connectionrequest.APConnectRequest;
import project.ing.soft.socket.request.connectionrequest.APReconnectRequest;
import project.ing.soft.socket.request.connectionrequest.ConnectionRequest;
import project.ing.soft.socket.request.connectionrequest.ConnectionRequestHandler;
import project.ing.soft.socket.response.connectionresponse.ConnectionEstabilishedResponse;
import project.ing.soft.socket.response.connectionresponse.ConnectionRefusedResponse;
import project.ing.soft.socket.ViewProxyOverSocket;
import project.ing.soft.socket.response.connectionresponse.NickNameAlreadyTakenResponse;
import project.ing.soft.view.IView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class APointSocket implements Callable<Boolean>, ConnectionRequestHandler, IAccessPoint {
    private Socket clientSocket;
    private AccessPointReal accessPointReal;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ViewProxyOverSocket viewProxy;

    public APointSocket(Socket clientSocket, AccessPointReal accessPointReal){
        this.clientSocket = clientSocket;
        this.accessPointReal = accessPointReal;
    }

    @Override
    public Boolean call() throws Exception{
        boolean nicknameAlreadyTaken;
        try {
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            do {
                try {
                    nicknameAlreadyTaken = false;
                    ConnectionRequest request = (ConnectionRequest) ois.readObject();
                    request.accept(this);
                } catch(NickNameAlreadyTakenException e){
                    oos.writeObject(new NickNameAlreadyTakenResponse(e));
                    //viewProxy.interrupt();
                    nicknameAlreadyTaken = true;
                }
            } while (nicknameAlreadyTaken);

        } catch (Exception e) {
            oos.writeObject(new ConnectionRefusedResponse(e));
            clientSocket.close();
            viewProxy.interrupt();
        }
        return true;
    }

    @Override
    public void handle(APConnectRequest request) throws Exception{
        String nickname = request.getNickname();
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois, nickname);
        connect(nickname, viewProxy);
        oos.writeObject(new ConnectionEstabilishedResponse());
        viewProxy.start();
    }

    @Override
    public void handle(APReconnectRequest request) throws Exception{
        String code = request.getGameToken();
        String nickname = request.getNickname();
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois, nickname);
        accessPointReal.reconnect(nickname, code, viewProxy);
        // Notify everything went well
        oos.writeObject(new ConnectionEstabilishedResponse());
        viewProxy.start();
    }


    @Override
    public IController connect(String nickname, IView clientView) throws Exception {
        return accessPointReal.connect(nickname, clientView);
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws Exception {
        return accessPointReal.reconnect(nickname, code, clientView);
    }
}
