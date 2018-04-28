package project.ing.soft.testsocket;


import project.ing.soft.Die;

import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.cards.toolcards.ToolCard;
import project.ing.soft.controller.IController;
import project.ing.soft.testsocket.request.*;
import project.ing.soft.testsocket.response.*;
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
import java.util.concurrent.atomic.AtomicBoolean;


public class ControllerProxy extends Thread implements IResponseHandler ,IController, Runnable {
    private int         port;
    private String      host;
    private ObjectInputStream  fromServer;
    private ObjectOutputStream toServer;
    private PrintWriter logger;
    private IView view;
    private ConcurrentLinkedQueue<IRequest> bufferReq;


    public ControllerProxy(String host, int port){
        this.host = host;
        this.port = port;
        this.logger = new PrintWriter(System.out);
        this.bufferReq = new ConcurrentLinkedQueue<>();

    }


    @Override
    public void run() {
        try(Socket aSocket = new Socket()) {
            aSocket.connect(new InetSocketAddress(host, port));
            aSocket.setSoTimeout(500);

            logger.println("Connected to the server");
            logger.flush();

            toServer = new ObjectOutputStream(aSocket.getOutputStream());
            toServer.flush();
            fromServer = new ObjectInputStream(aSocket.getInputStream());

            flushRequests();
            while( !aSocket.isClosed()){

                try {
                    //blocked waiting for a response from server
                    //BUT this thread could be interrupted calling interrupt. This will cause the flush of
                    //every Request in the queue
                    IResponse aResponse = (IResponse) fromServer.readObject();
                    if (aResponse != null) {
                        this.visit(aResponse);
                    }




                } catch (SocketTimeoutException ex){
                    flushRequests();
                }

            }



        }catch (Exception ex){
            ex.printStackTrace(System.out);
        }finally {
            closeStreams();


        }
    }
    private synchronized void flushRequests() throws Exception{
        for (IRequest aRequest : bufferReq) {
            toServer.writeObject(aRequest);
            logger.println("Forwarded a request " + aRequest.getClass());
            bufferReq.remove(aRequest);
        }
    }

    @Override
    public void visit(IResponse aResponse) throws Exception{
        logger.println( "Received a response " + aResponse.getClass());
        aResponse.accept(this);
    }

    @Override
    public void handle(InformationResponse aResponse) throws Exception{
        logger.println("an erroneous response was taken");
    }

    @Override
    public void handle(ParticipationConfirmedResponse aResponse) {
        logger.println( "an erroneous response was taken");
    }

    @Override
    public void handle(CreationGameResponse aResponse) throws Exception {
        logger.println("an erroneous response was taken");
    }

    @Override
    public void handle(ExceptionalResponse aResponse) throws Exception {
        if(view != null)
            view.handleException(aResponse.getEx());
    }

    @Override
    public void handle(EventResponse aResponse) throws Exception {
        if(view != null)
            view.update(aResponse.getEvent());
    }


    private void closeStreams(){
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



    public synchronized void addToQueue(IRequest aNewRequest) throws Exception{
        bufferReq.add(aNewRequest);
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
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        addToQueue(new PlayToolCardRequest(nickname, aToolCard));
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        addToQueue(new EndTurnRequest(nickname));
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        addToQueue(new choosePatternRequest(nickname, windowCard, side));
    }

    @Override
    public void joinTheGame(String nickname, IView view) throws Exception {
        this.view = view;
        addToQueue(new joinTheGameRequest(nickname));
    }
}

