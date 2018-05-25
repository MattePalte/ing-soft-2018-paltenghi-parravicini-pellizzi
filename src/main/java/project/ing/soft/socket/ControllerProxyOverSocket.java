package project.ing.soft.socket;


import org.junit.Assert;
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
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ControllerProxyOverSocket extends Thread implements IResponseHandler ,IController, Runnable {
    private Socket clientSocket;

    private ObjectInputStream  fromServer;
    private ObjectOutputStream toServer;
    private PrintWriter logger;
    private IView view;

    private final ConcurrentLinkedQueue<AbstractRequest> toSendList;
    private final ArrayList<AbstractRequest> toAckList;


    public ControllerProxyOverSocket(IView view, Socket clientSocket, ObjectOutputStream oos, ObjectInputStream ois){
        this.view = view;
        this.clientSocket = clientSocket;
        this.toServer = oos;
        this.fromServer = ois;
        this.logger = new PrintWriter(System.out);
        this.toSendList = new ConcurrentLinkedQueue<>();
        this.toAckList  = new ArrayList<>();

    }


    @Override
    public void run() {

        try {
            //to support complete asynchronous operation between client and server
            clientSocket.setSoTimeout(500);

            logger.println("Connected to the server");
            logger.flush();

            /*toServer = new ObjectOutputStream(clientSocket.getOutputStream());
            toServer.flush();
            fromServer = new ObjectInputStream(clientSocket.getInputStream());*/


            while( !clientSocket.isClosed()){

                try {

                    //blocked waiting for a response from server
                    //BUT this thread could be interrupted calling interrupt. This will cause the flush of
                    //every Request in the queue

                    IResponse aResponse = (IResponse) fromServer.readObject();
                    this.visit(aResponse);


                } catch (SocketTimeoutException ex){

                    synchronized (toSendList){
                        for (AbstractRequest aRequest : toSendList) {
                            if(toAckList.indexOf(null) == -1)
                                toAckList.add(null);
                            aRequest.setId(toAckList.indexOf(null));
                            toAckList.set(aRequest.getId(), aRequest);

                            toServer.writeObject(aRequest);
                            logger.println("Forwarded a request " + aRequest.getClass());
                            toSendList.remove(aRequest);

                        }

                    }

                }

            }



        }catch (Exception ex){
            ex.printStackTrace(logger);
        }finally {

            try {
                if (fromServer != null){
                    fromServer.close();
                    fromServer = null;
                }
            }catch(IOException ignored){
                logger.println( "ObjectInputStream was already closed");
            }

            try{
                if(toServer != null) {
                    toServer.close();
                    toServer = null;
                }
            } catch(IOException ignored) {
                logger.println( "ObjectOutputStream was already closed");
            }


        }
    }


    public void visit(IResponse aResponse) {
        logger.println( "Received a response " + aResponse.getClass());
        aResponse.accept(this);
    }


    @Override
    public void handle(InformationResponse aResponse) {
        logger.println("an information response was received");
    }

    @Override
    public void handle(CreationGameResponse aResponse) {
        logger.println("a game has been created");
    }

    @Override
    public void handle(ExceptionalResponse aResponse) {
        AbstractRequest aRequest = toAckList.get(aResponse.getId());
        toAckList.set(aResponse.getId(), null);

        if(aRequest != null) {
            synchronized (toAckList) {
                aRequest.setBeenHandled(true);
                aRequest.setException(aResponse.getEx());
                toAckList.notifyAll();
            }
        }
    }

    @Override
    public void handle(AllRightResponse aResponse) {
        AbstractRequest aRequest = toAckList.get(aResponse.getId());
        toAckList.set(aResponse.getId(), null);

        if(aRequest != null)
            synchronized (toAckList) {
                aRequest.setBeenHandled(true);
                toAckList.notifyAll();
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






    public void addToQueue(AbstractRequest aNewRequest) throws Exception{
        //add to request tobeSent
        synchronized (toSendList) {
            toSendList.add(aNewRequest);
        }
        //wait for an execution
        // aNewRequest should be used, but sonarlint complaints
        synchronized(toAckList){
            while (!aNewRequest.beenHandled()) {
                toAckList.wait();
            }

            if(aNewRequest.hasException()) {
                throw aNewRequest.getException();
            }
        }
    }


    @Override
    public void requestUpdate() throws Exception {
        addToQueue(new UpdateRequest());
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        addToQueue(new PlaceDieRequest(nickname, aDie, colIndex, rowIndex));
    }

    @Override
    public void PlayToolCard(String nickname, ToolCard aToolCard) throws Exception {
        addToQueue(new PlayToolCardRequest(nickname, aToolCard));
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        addToQueue(new EndTurnRequest(nickname));
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        addToQueue(new ChoosePatternRequest(nickname, windowCard, side));
    }

    @Override
    public void chooseDie(Die aDie) {
        // TODO: implement method and create request
    }

    @Override
    public String getControllerSecurityCode() {
        throw new UnsupportedOperationException();
    }

}

