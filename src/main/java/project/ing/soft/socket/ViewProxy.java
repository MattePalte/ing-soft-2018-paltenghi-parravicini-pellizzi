package project.ing.soft.socket;

import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.socket.request.*;
import project.ing.soft.socket.response.*;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.Socket;


public class ViewProxy implements IView,IRequestHandler, Runnable {
    private IController controller;
    private Socket aSocket;

    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;
    private PrintStream log;


    public ViewProxy(Socket aSocket)throws Exception {
        this.aSocket = aSocket;
        this.toClient   = new ObjectOutputStream(aSocket.getOutputStream());
        this.toClient.flush();
        this.fromClient = new ObjectInputStream(aSocket.getInputStream());
        this.log = new PrintStream(System.out);
    }

    @Override
    public void run() {
        try {


            while (!aSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                // readObject doesn't wait for the inputStream to have data: it throws EOFException
                // Must do a requestQueue on which we have to synchronize readObject call
                AbstractRequest aRequest = (AbstractRequest) fromClient.readObject();
                this.visit(aRequest);

            }
        }catch (EOFException ignored){
            log.println("EOFException occured");
        }catch (ClassNotFoundException ex) {
            log.println( "A class wasn't found "+ ex );
        } catch (Exception ex){
            log.println(  "An error occurred while writing/reading objects "+ ex);
        }finally {
            log.println("disconnected");
        }

    }


    public void interrupt() {
        //super.interrupt();

        try {
            if(fromClient != null)
                fromClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(toClient != null)
                toClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(aSocket != null)
                aSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //region handle request. pass request to the real controller
    @Override
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
    public void handle(ParticipationRequest aRequest) throws Exception {
        throw new UnsupportedOperationException();
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
        this.controller.placeDie(aRequest.getNickname(), aRequest.getTheDie(), aRequest.getRowIndex(), aRequest.getColIndex());
    }

    @Override
    public void handle(UpdateRequest aRequest) throws Exception {
        this.controller.requestUpdate();
    }

    @Override
    public void handle(PlayToolCardRequest aRequest) throws Exception {
        this.controller.playToolCard(aRequest.getNickname(), aRequest.getaToolCard());
    }

    @Override
    public void handle(EndTurnRequest aRequest) throws Exception {

        this.controller.endTurn(aRequest.getNickname());
    }

    @Override
    public void handle(ChoosePatternRequest aRequest) throws Exception {
        this.controller.choosePattern(aRequest.getNickname(), aRequest.getWindowCard(), aRequest.getSide());
    }

    @Override
    public void handle(JoinTheGameRequest aRequest) throws Exception {
        this.controller.joinTheGame(aRequest.getNickname(), this);
    }



    //endregion

    //region IView
    private void send(IResponse aResponse) throws Exception {
        log.println("Forwarding a response "+ aResponse.getClass());
        toClient.writeObject(aResponse);
    }

    @Override
    public void update(Event event) throws Exception {
       send(new EventResponse(event));
    }

    @Override
    public void attachController(IController gameController) throws Exception {
        this.controller = gameController;
    }
    //endregion
}