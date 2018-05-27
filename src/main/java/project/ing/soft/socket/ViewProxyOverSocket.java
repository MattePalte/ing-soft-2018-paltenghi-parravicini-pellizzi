package project.ing.soft.socket;

import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.socket.request.*;
import project.ing.soft.socket.response.*;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.Socket;



public class ViewProxyOverSocket implements IView,IRequestHandler, Runnable {
    private GameController gameController;
    private Socket aSocket;

    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;
    private PrintStream log;

    private String nickname;


    public ViewProxyOverSocket(Socket aSocket, ObjectOutputStream oos, ObjectInputStream ois, String nickname) throws IOException {
        this.aSocket = aSocket;
        this.toClient   = oos;
        this.toClient.flush();
        this.fromClient = ois;
        this.log = new PrintStream(System.out);
        this.nickname = nickname;
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
        }catch (EOFException ignored){
            log.println("EOFException occurred");
        }catch (ClassNotFoundException ex) {
            log.println( "A class wasn't found "+ ex );
        } catch (Exception ex){
            log.println(  "An error occurred while writing/reading objects "+ ex);
            gameController.markAsDisconnected(nickname);
            ex.printStackTrace(log);
        }finally {
            log.println("disconnected");
        }

    }

    @Override
    public PrintStream getPrintStream() {
        return log;
    }


    public void interrupt() {
        //super.interrupt()

        try {
            if(fromClient != null)
                fromClient.close();
        } catch (IOException e) {
            e.printStackTrace(log);
        }

        try {
            if(toClient != null)
                toClient.close();
        } catch (IOException e) {
            e.printStackTrace(log);
        }

        try {
            if(aSocket != null)
                aSocket.close();
        } catch (IOException e) {
            e.printStackTrace(log);
        }
    }

    //region handle request. Pass request to the real gameController
    public void visit(AbstractRequest aRequest) throws Exception {
        log.println("Request received"+aRequest.getClass());
        try {
            aRequest.accept(this);
            toClient.writeObject(new AllRightResponse(aRequest.getId()));
        }catch (Exception ex){
            toClient.writeObject(new ExceptionalResponse(ex, aRequest.getId()));
        }
    }


    @Override
    public void handle(InformationRequest aRequest) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(CreationGameRequest aRequest) throws Exception {
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
        log.println("Forwarding a response "+ aResponse.getClass());
        toClient.writeObject(aResponse);
    }

    //endregion
}
