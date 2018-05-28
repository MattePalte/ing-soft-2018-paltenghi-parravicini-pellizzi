package project.ing.soft.socket;

import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.socket.request.*;
import project.ing.soft.socket.response.*;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ViewProxyOverSocket extends Thread implements IView,IRequestHandler {
    private GameController gameController;
    private Socket aSocket;

    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;
    private Logger log;

    private String nickname;


    public ViewProxyOverSocket(Socket aSocket, ObjectOutputStream oos, ObjectInputStream ois, String nickname) throws IOException {
        this.aSocket        = aSocket;
        this.toClient       = oos;
        this.toClient.flush();
        this.fromClient     = ois;
        this.log            = Logger.getLogger(this.getClass().getCanonicalName()+"(" +nickname+")");
        this.log.setLevel(Level.OFF);
        this.nickname       = nickname;
    }

    @Override
    public void run() {
        try {


            while (!aSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                // readObject doesn't wait for the inputStream to have data: it throws EOFException
                // Must do a requestQueue on which we have to synchronize readObject call
                AbstractRequest aRequest = (AbstractRequest) fromClient.readObject();
                this.visit(aRequest);
                toClient.reset();

            }
        }catch (Exception ex){
            log.log(Level.SEVERE,"Exception occurred", ex);
            gameController.markAsDisconnected(nickname);

        }finally {
            log.log(Level.INFO,"disconnected");
        }

    }



    public void interrupt() {

        try {
            if(fromClient != null)
                fromClient.close();
        } catch (IOException e) {
            log.log(Level.SEVERE,"Error while closing ObjectInputStream",  e);
        }

        try {
            if(toClient != null)
                toClient.close();
        } catch (IOException e) {
            log.log(Level.SEVERE,"Error while closing ObjectOutputStream",  e);
        }

        try {
            if(aSocket != null)
                aSocket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE,"Error while closing socket",  e);
        }
    }

    //region handle request. Pass request to the real gameController
    public void visit(AbstractRequest aRequest) throws Exception {
        log.log(Level.INFO, "Request {0} received {1}",new Object[]{aRequest.getId(), aRequest});
        try {
            aRequest.accept(this);
            toClient.writeObject(new AllRightResponse(aRequest.getId()));
            log.log(Level.INFO, "Request {0} finished correctly",aRequest.getId());
        }catch (Exception ex){
            toClient.writeObject(new ExceptionalResponse(ex, aRequest.getId()));
            log.log(Level.INFO, "Request {0} ended with error {1}",new Object[]{aRequest.getId(), ex});
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
    public void handle(PlaceDieRequest aRequest) throws Exception {
        this.gameController.placeDie(aRequest.getNickname(), aRequest.getTheDie(), aRequest.getRowIndex(), aRequest.getColIndex());
    }

    @Override
    public void handle(UpdateRequest aRequest) throws Exception {
        this.gameController.requestUpdate();
    }

    @Override
    public void handle(PlayToolCardRequest aRequest) throws Exception {
        this.gameController.playToolCard(aRequest.getNickname(), aRequest.getaToolCard());
    }

    @Override
    public void handle(EndTurnRequest aRequest) throws Exception {
        this.gameController.endTurn(aRequest.getNickname());
    }

    @Override
    public void handle(ChoosePatternRequest aRequest) throws Exception {
        this.gameController.choosePattern(aRequest.getNickname(), aRequest.getWindowCard(), aRequest.getSide());
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

    private void send(IResponse aResponse) throws IOException {
        log.log(Level.INFO, "Forwarding a response {0} ", aResponse);
        toClient.writeObject(aResponse);
    }

    //endregion
}
