package project.ing.soft.socket;

import org.junit.Assert;
import project.ing.soft.Settings;
import project.ing.soft.model.Die;

import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.controller.IController;
import project.ing.soft.socket.request.*;
import project.ing.soft.socket.response.*;
import project.ing.soft.view.IView;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControllerProxyOverSocket extends Thread implements IResponseHandler ,IController, Runnable {
    private Socket socket;

    private final ObjectInputStream  fromServer;
    private final ObjectOutputStream toServer;
    private final Logger logger;
    private IView view;

    private final Queue<AbstractRequest> requestBuffer;
    private final ArrayList<AbstractRequest> toAckList;


    ControllerProxyOverSocket(IView view, Socket socket, ObjectOutputStream oos, ObjectInputStream ois){
        this.view           = view;
        this.socket         = socket;
        this.toServer       = oos;
        this.fromServer     = ois;
        this.toAckList      = new ArrayList<>();
        this.requestBuffer  = new ArrayDeque<>();
        this.logger         = Logger.getLogger(Objects.toString(this));
        this.logger.setLevel(Settings.instance().getDefaultLoggingLevel());

    }


    @Override
    public void run() {
        //to support complete asynchronous operation between client and server
        Thread eventFlusher = new Thread(this::flushEvents);
        eventFlusher.setDaemon(true);
        eventFlusher.start();
        logger.log(Level.INFO, "Socket thread starting...");
        try {



            while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                IResponse aResponse = (IResponse) fromServer.readObject();
                this.visit(aResponse);

            }
        }catch(ClassNotFoundException | IOException ex){
            logger.log(Level.SEVERE, "Error occurred while reading objects", ex);
        }finally {
            closeSocket();
        }

        eventFlusher.interrupt();
    }

    private void closeSocket() {
        try {
            if (fromServer != null){
                fromServer.close();
            }
        }catch(IOException ex){
            logger.log(Level.SEVERE, "Error while closing ObjectInputStream", ex);
        }

        try{
            if(toServer != null) {
                toServer.close();
            }
        } catch(IOException ex) {
            logger.log(Level.SEVERE, "Error while closing ObjectOutputStream", ex);
        }
        super.interrupt();
    }

    private void flushEvents(){
        try{
        while(!Thread.currentThread().isInterrupted() && !this.isInterrupted()){

            AbstractRequest aNewRequest;
            synchronized (requestBuffer){
                while (requestBuffer.isEmpty())
                    requestBuffer.wait();
                aNewRequest = requestBuffer.poll();
            }

            if(aNewRequest != null) {

                synchronized (toAckList) {
                    if (toAckList.indexOf(null) == -1)
                        toAckList.add(null);
                    aNewRequest.setId(toAckList.indexOf(null));
                    toAckList.set(aNewRequest.getId(), aNewRequest);
                }
                logger.log(Level.INFO, "A new request was found. Moving to toAckList with id {0}", aNewRequest.getId());
                toServer.writeObject(aNewRequest);
                logger.log(Level.INFO, "Forwarded a request {0}", aNewRequest);

            }

        }
        }catch (InterruptedException interrupt){
            logger.log(Level.INFO,"FlushRequest thread interrupt exception received. finishing exceution");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.log(Level.SEVERE,"FlushRequest thread received an error while writing", e);

        }

    }

    private void visit(IResponse aResponse) {
        logger.log(Level.INFO, "Received {0} from server" , aResponse);
        aResponse.accept(this);
    }


    @Override
    public void handle(InformationResponse aResponse) {
        logger.log(Level.INFO,"an information response was received");
    }

    @Override
    public void handle(CreationGameResponse aResponse) {
        logger.log(Level.INFO,"a game has been created");
    }

    @Override
    public void handle(ExceptionalResponse aResponse) {
        logger.log(Level.INFO,"The action {0} ended up erroneously", aResponse.getId());

        synchronized (toAckList) {
            AbstractRequest aRequest = toAckList.get(aResponse.getId());
            if(aRequest != null) {
                toAckList.set(aResponse.getId(), null);
                aRequest.setBeenHandled(true);
                aRequest.setException(aResponse.getEx());
                toAckList.notifyAll();
            }
        }
    }

    @Override
    public void handle(AllRightResponse aResponse) {
        logger.log(Level.INFO,"The action {0} ended up correctly", aResponse.getId());
        synchronized (toAckList) {
            AbstractRequest aRequest = toAckList.get(aResponse.getId());
            if(aRequest != null) {
                toAckList.set(aResponse.getId(), null);
                aRequest.setBeenHandled(true);
                toAckList.notifyAll();
            }
        }
    }


    @Override
    public void handle(EventResponse aResponse) {

        try {
            if (view != null)
                view.update(aResponse.getEvent());
        }catch (IOException exc){
            Assert.fail("A local view has raised an io exception. Panic! ");
        }
    }

    /**
     * The function pass the request supplied as an argument to the thread that is in charge
     * of communicating with the server. Then it waits until the server has executed the operation
     * and a corresponding response with the same id get back.
     * @param aNewRequest a request for execution of remote method
     * @throws Exception if the supplied request resulted in a exception on the backend.
     */
    private void addToQueue(AbstractRequest aNewRequest) throws Exception{
        //add to request tobeSent
        try {
            synchronized (requestBuffer) {
                requestBuffer.add(aNewRequest);


                requestBuffer.notifyAll();
            }
            //wait for execution
            synchronized (toAckList){
                //aNewRequest should be used, but Sonarlint complaints
                while (!aNewRequest.beenHandled()) {
                    toAckList.wait();
                }
            }
        }catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }

        if(aNewRequest.hasException()) {
            throw aNewRequest.getException();
        }

    }
    //region IController interface
    @Override
    public void requestUpdate() throws Exception {
        logger.log(Level.INFO,"An update was requested");
        addToQueue(new UpdateRequest());
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        logger.log(Level.INFO,"{0} request to place die {1} on ({2},{3})", new Object[]{nickname, aDie, rowIndex, colIndex});
        addToQueue(new PlaceDieRequest(nickname, aDie, colIndex, rowIndex));
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        logger.log(Level.INFO,"{0} request to play the ToolCard: {1} ", new Object[]{nickname, aToolCard});
        addToQueue(new PlayToolCardRequest(nickname, aToolCard));
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        logger.log(Level.INFO,"{0} request to end his turn", nickname);
        addToQueue(new EndTurnRequest(nickname));
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        logger.log(Level.INFO,"{0} inform the game that he has chosen {1}", new Object[]{nickname, side ? windowCard.getFrontPattern().getTitle(): windowCard.getRearPattern().getTitle()});
        addToQueue(new ChoosePatternRequest(nickname, windowCard, side));
    }

    @Override
    public String getControllerSecurityCode() {
        return "fake-code-not-yet-implemented-in-proxy";
    }
    //endregion
}

