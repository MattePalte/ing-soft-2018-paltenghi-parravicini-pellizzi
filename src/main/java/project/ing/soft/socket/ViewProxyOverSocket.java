package project.ing.soft.socket;

import project.ing.soft.Settings;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.socket.request.*;
import project.ing.soft.socket.response.*;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is intended to be a server representation of the user,and
 * as an entry point that allow user to interact with the server.
 * Its main concern is take into action user's requests as he was the associated player
 * An instance of this class is usually obtained by invoking{@link APointSocket#connect(String, IView)}
 * or {@link APointSocket#reconnect(String, String, IView)}
 */
public class ViewProxyOverSocket extends Thread implements IView,IRequestHandler {
    private GameController              gameController;
    private Socket                      aSocket;

    private String                      associatedNickname;
    private final AtomicBoolean         isConnected;
    private boolean                     isStarted;
    private final ArrayList<IResponse>  buffer;
    private final ObjectOutputStream    toClient;
    private final ObjectInputStream     fromClient;

    private final Logger logger;


    /**
     * The viewProxyOverSocket take as arguments the name of the player that has been previously associated
     * to the game and the information associated with socket connection.
     * Since the {@link APProxySocket} has already used the socket to ensure that the remote player is the legitimate
     * owner of {@param nickname} identifier, It has to open the socket and deal with oos/ois
     * @param aSocket that is related to ObjectOutput/InputStream parameters
     * @param oos that can be used to write data through the socket
     * @param ois that can be used to read data through the socket
     * @param nickname that can uniquely associate the remote player in the game.
     * @throws IOException if an error occurred with OOS.
     */
    ViewProxyOverSocket(Socket aSocket, ObjectOutputStream oos, ObjectInputStream ois, String nickname) throws IOException {
        this.aSocket        = aSocket;
        this.toClient       = oos;
        this.toClient.flush();
        this.fromClient     = ois;
        this.logger = Logger.getLogger(this.getClass().getCanonicalName()+"(" +nickname+")");
        this.logger.setLevel(Settings.instance().getDefaultLoggingLevel());
        this.associatedNickname = nickname;
        this.isConnected    = new AtomicBoolean(true);
        this.isStarted      = false;
        this.buffer         = new ArrayList<>();
    }

    /**
     * This is the main part of the class. The thread waits for user request and handle it.
     * Every object received from {@link #fromClient} is a subclass of AbstractRequest which
     * embed every necessary information to carry out the operation identified by the
     * particular type of AbstractRequest itself.
     * Every request in this way is associated to a corresponding action and then to a response that
     * has to be returned to the client. This is accomplished by {@link #visit(AbstractRequest)}
     *
     *
     * The view does not only enable information flow from client to server,
     * it is also responsible to transmit information back to the client supplied to {@link #update(Event)}.
     *
     * When the view is created the OOS,OIS could be occupied by the {@link APointSocket}.
     * Because of that any message that is sent to {@link #update(Event)} has to be buffered and sent
     * when {@link Thread#start()} is actually invoked.
     * Any error during transmission resemble a disconnection event that has to be notified.
     */
    @Override
    public void run() {
        try {

            synchronized (toClient){
                for (IResponse res: buffer ) {
                    logger.log(Level.INFO,"message {0} found in the buffer", res);
                    toClient.writeObject(res);
                }
                isStarted = true;
            }
            while (!aSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                // readObject doesn't wait for the inputStream to have data: it throws EOFException
                // Must do a requestQueue on which we have to synchronize readObject call
                AbstractRequest aRequest = (AbstractRequest) fromClient.readObject();
                this.visit(aRequest);
                synchronized (toClient) {
                    toClient.reset();
                }
            }
        }catch(Exception ex){
            logger.log(Level.INFO, "{0} disconnected", associatedNickname);
            // else user asked for reconnection, so disconnection is done elsewhere
            this.interrupt();
        }

    }


    /**
     * An interruption is required when the connection is not needed anymore.
     */
    @Override
    public void interrupt() {
        // if user asked for reconnection calls interrupt, and the boolean
        // is set to false as a flag to distinguish from disconnection
        // due to network problems or client crashes
        if(isConnected.getAndSet(false)) {
            super.interrupt();
            logger.log(Level.INFO, "Event dispatcher marked {0} as disconnected ", associatedNickname);

            if(gameController !=null) {
                gameController.markAsDisconnected(associatedNickname);
            }

            try {
                if (fromClient != null)
                    fromClient.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while closing ObjectInputStream", e);
            }

            try {
                if (toClient != null)
                    toClient.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while closing ObjectOutputStream", e);
            }

            try {
                if (aSocket != null)
                    aSocket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while closing socket", e);
            }
        }
    }

    //region handle request. Pass request to the real gameController

    /**
     * entrypoint for the visitor pattern that discern beetween request supplied and
     * based on their type it retrieves the necessary information to carry out the operation.
     * The action is then carried out on the {@link #gameController}, the result is given back to the client.
     * Note: no method has been recalled on AbstractRequest subclass in order to avoid code injection from client.
     * Note: nickname supplied by the AbstractRequest is ignored in order to avoid user substitution.
     * @param aRequest to be mapped to the corresponding action
     * @throws IOException it's raised whether a trasmission of a response to the client ended
     *          with an error.
     */
    private void visit(AbstractRequest aRequest) throws IOException {
        logger.log(Level.INFO, "Request {0} received {1}",new Object[]{aRequest.getId(), aRequest});
        try {
            aRequest.accept(this);
            send(new AllRightResponse(aRequest.getId()));
            logger.log(Level.INFO, "Request {0} finished correctly",aRequest.getId());
        }catch (Exception ex){
            send(new ExceptionalResponse(ex, aRequest.getId()));
            logger.log(Level.INFO, "Request {0} ended with error {1}",new Object[]{aRequest.getId(), ex});
        }
    }


    @Override
    public void handle(InformationRequest aRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(CreationGameRequest aRequest){
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(UpdateRequest aRequest) {
        logger.log(Level.INFO, "Player {0} request an update", associatedNickname);
        this.gameController.requestUpdate();
    }

    @Override
    public void handle(PlaceDieRequest aRequest) throws Exception {
        logger.log(Level.INFO, "Player {0} request to place die {1} on ({2},{3})", new Object[]{aRequest.nickname, aRequest.aDie, aRequest.rowIndex, aRequest.colIndex});
        this.gameController.placeDie(associatedNickname, aRequest.aDie, aRequest.rowIndex, aRequest.colIndex);
    }

    @Override
    public void handle(PlayToolCardRequest aRequest) throws Exception {
        logger.log(Level.INFO,"{0} request to play the ToolCard: {1} ", new Object[]{aRequest.nickname, aRequest.aToolCard});
        this.gameController.playToolCard(associatedNickname, aRequest.aToolCard);
    }

    @Override
    public void handle(EndTurnRequest aRequest) throws Exception {
        logger.log(Level.INFO,"{0} request to end his turn", aRequest.nickname);
        this.gameController.endTurn(associatedNickname);
    }

    @Override
    public void handle(ChoosePatternRequest aRequest) throws Exception {
        logger.log(Level.INFO,"{0} inform the game that he has chosen {1}", new Object[]{aRequest.nickname, aRequest.frontSide ? aRequest.windowCard.getFrontPattern().getTitle(): aRequest.windowCard.getRearPattern().getTitle()});
        this.gameController.choosePattern(associatedNickname, aRequest.windowCard, aRequest.frontSide);
    }

    //endregion

    //region IView

    @Override
    public void attachController(IController gameController) {
        this.gameController = (GameController) gameController;
    }

    @Override
    public void update(Event event) throws IOException {
        send(new EventResponse(event));
    }

    //endregion

    /**
     * this is a helper method that can be used to trasmit
     * @param aResponse to the client
     * @throws IOException whether a network error is raised
     */
    private void send(IResponse aResponse) throws IOException {
        logger.log(Level.INFO, "Forwarding a response {0} ", aResponse);
        synchronized (toClient) {
            if(isStarted) {
                toClient.writeObject(aResponse);
            }else {
                buffer.add(aResponse);
            }
        }
    }
}
