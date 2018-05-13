package project.ing.soft.socket;


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


public class ControllerProxy extends Thread implements IResponseHandler ,IController, Runnable {
    private int         port;
    private String      host;

    private ObjectInputStream  fromServer;
    private ObjectOutputStream toServer;
    private PrintWriter logger;
    private IView view;

    private final ConcurrentLinkedQueue<AbstractRequest> toSendList;
    private final ArrayList<AbstractRequest> toAckList;


    public ControllerProxy(String host, int port){
        this.host = host;
        this.port = port;
        this.logger = new PrintWriter(System.out);
        this.toSendList = new ConcurrentLinkedQueue<>();
        this.toAckList  = new ArrayList<>();

    }


    @Override
    public void run() {

        try(Socket aSocket = new Socket()) {
            aSocket.connect(new InetSocketAddress(host, port));
            //to support complete asynchronous operation between client and server
            aSocket.setSoTimeout(500);

            logger.println("Connected to the server");
            logger.flush();

            toServer = new ObjectOutputStream(aSocket.getOutputStream());
            toServer.flush();
            fromServer = new ObjectInputStream(aSocket.getInputStream());


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


    @Override
    public void visit(IResponse aResponse) throws Exception{
        logger.println( "Received a response " + aResponse.getClass());
        aResponse.accept(this);
    }

    @Override
    public void handle(InformationResponse aResponse) throws Exception{
        logger.println("an information response was received");
    }

    @Override
    public void handle(CreationGameResponse aResponse) throws Exception {
        logger.println("a game has been created");
    }

    @Override
    public void handle(ExceptionalResponse aResponse) throws Exception {
        AbstractRequest aRequest = toAckList.get(aResponse.getId());
        toAckList.set(aResponse.getId(), null);

        if(aRequest != null) {
            synchronized (aRequest) {
                aRequest.setBeenHandled(true);
                aRequest.setException(aResponse.getEx());
                aRequest.notifyAll();
            }
        }
    }

    @Override
    public void handle(AllRightResponse aResponse) throws Exception {
        AbstractRequest aRequest = toAckList.get(aResponse.getId());
        toAckList.set(aResponse.getId(), null);

        if(aRequest != null)
            synchronized (aRequest) {
                aRequest.setBeenHandled(true);
                aRequest.notifyAll();
            }
    }


    @Override
    public void handle(EventResponse aResponse) throws Exception {
        if(view != null)
            view.update(aResponse.getEvent());
    }






    public void addToQueue(AbstractRequest aNewRequest) throws Exception{
        //add to request tobeSent
        synchronized (toSendList) {
            toSendList.add(aNewRequest);
        }
        //wait for an execution
        synchronized(aNewRequest){
            while (!aNewRequest.beenHandled()) {
                aNewRequest.wait();
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
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
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
    public void joinTheGame(String nickname, IView view) throws Exception {
        this.view = view;
        addToQueue(new JoinTheGameRequest(nickname));
    }
}

