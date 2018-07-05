package project.ing.soft.socket;

import project.ing.soft.accesspoint.AccessPointReal;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.request.connectionrequest.APConnectRequest;
import project.ing.soft.socket.request.connectionrequest.APReconnectRequest;
import project.ing.soft.socket.request.connectionrequest.ConnectionRequest;
import project.ing.soft.socket.request.connectionrequest.ConnectionRequestHandler;
import project.ing.soft.socket.response.connectionresponse.ConnectionEstabilishedResponse;
import project.ing.soft.socket.response.connectionresponse.ConnectionRefusedResponse;
import project.ing.soft.socket.response.connectionresponse.NickNameAlreadyTakenResponse;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * A technology-specific implementation of {@link project.ing.soft.accesspoint.IAccessPoint} in
 * socket context that acts at the server side.
 * On the other endpoint a {@link APProxySocket} exposes these capabilities to the client.
 * The APProxySocket permit socket users to access  {@link project.ing.soft.accesspoint.AccessPointReal}
 * which is in charge of admitting and discharging player connection request.
 * It's created by {@link SocketListener} after the {@link ServerSocket#accept()}
 */
public class APointSocket implements Callable<Boolean>, ConnectionRequestHandler, IAccessPoint {
    private final Socket clientSocket;
    private final AccessPointReal accessPointReal;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ViewProxyOverSocket viewProxy;

    /**
     * When a TCP connection it's established this object it's created in order to handle user
     * connection request.
     * @param clientSocket that give access to I/O capabilities
     * @param accessPointReal that is the {@link IAccessPoint} to be decorated
     */
    public APointSocket(Socket clientSocket, AccessPointReal accessPointReal){
        this.clientSocket = clientSocket;
        this.accessPointReal = accessPointReal;
    }

    @Override
    public Boolean call() throws Exception{
        boolean nicknameAlreadyTaken;

        ois = new ObjectInputStream(clientSocket.getInputStream());
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        do {
            try {
                nicknameAlreadyTaken = false;
                ConnectionRequest request = (ConnectionRequest) ois.readObject();
                request.accept(this);
            } catch(NickNameAlreadyTakenException e){
                //notify error to the view
                oos.writeObject(new NickNameAlreadyTakenResponse(e));
                nicknameAlreadyTaken = true;
            } catch (Exception e) {
                //notify error to the view
                oos.writeObject(new ConnectionRefusedResponse(e));
                clientSocket.close();
                viewProxy.interrupt();
                return true;
            }
        } while (nicknameAlreadyTaken);

        return true;
    }

    //region dispatcher
    /**
     * These methods represent half of the implementation of a visitor pattern
     * dispatch the {@link ConnectionRequest} to the correct method
     * @param request request to be handled
     * @throws IOException if {@link AccessPointReal#connect(String, IView)} throws an exception
     * @throws NickNameAlreadyTakenException if {@link AccessPointReal#connect(String, IView)} throws an exception
     * @throws GameInvalidException if {@link AccessPointReal#connect(String, IView)} throws an exception
     */
    @Override
    public void handle(APConnectRequest request) throws IOException, NickNameAlreadyTakenException, GameInvalidException {
        String nickname = request.nickname;
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois, nickname);
        accessPointReal.connect(nickname, viewProxy);
        // POST CONNECT ->
        oos.writeObject(new ConnectionEstabilishedResponse());
        viewProxy.start();
    }

    /**
     * These methods represent half of the implementation of a visitor pattern
     * dispatch the {@link ConnectionRequest} to the correct method
     * @param request request to be handled
     * @throws IOException if {@link AccessPointReal#connect(String, IView)} throws an exception
     *
     */
    @Override
    public void handle(APReconnectRequest request) throws IOException, GameInvalidException, ActionNotPermittedException, CodeInvalidException {
        String code = request.gameToken;
        String nickname = request.nickname;
        viewProxy = new ViewProxyOverSocket(clientSocket, oos, ois, nickname);
        accessPointReal.reconnect(nickname, code, viewProxy);
        // POST CONNECT ->
        // Notify everything went well
        oos.writeObject(new ConnectionEstabilishedResponse());
        viewProxy.start();
    }

    //endregion

    /**
     *This class actually does not need to extend {@link IAccessPoint} to expose
     * its capabilities in socket context. This has been done in order to get an homogeneous
     * view of the connection method between Socket and Rmi.
     */
    @Override
    public IController connect(String nickname, IView clientView) throws Exception {
        throw new NoSuchMethodException();
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws Exception {
        throw new NoSuchMethodException();
    }

}
