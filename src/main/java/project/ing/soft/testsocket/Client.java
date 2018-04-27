package project.ing.soft.testsocket;

import project.ing.soft.Die;
import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.cards.toolcards.ToolCard;
import project.ing.soft.controller.IController;
import project.ing.soft.testsocket.request.CreationGameRequest;
import project.ing.soft.testsocket.request.IRequest;
import project.ing.soft.testsocket.request.InformationRequest;
import project.ing.soft.testsocket.response.*;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Client implements IResponseHandler ,Runnable {
    private int         port;
    private String      host;
    private PrintWriter out;
    private ObjectInputStream  fromServer;
    private ObjectOutputStream toServer;

    private Client(String host, int port){
        this.host = host;
        this.port = port;
        this.out  = new PrintWriter(System.out);
    }

    @Override
    public void run() {
       connectAndHandleMessages();
    }

    private void connectAndHandleMessages(){
        try(Socket aSocket = new Socket()) {
            aSocket.connect(new InetSocketAddress(host, port));

            fromServer = new ObjectInputStream(aSocket.getInputStream());
            toServer = new ObjectOutputStream(aSocket.getOutputStream());

            while( !Thread.currentThread().isInterrupted() && !aSocket.isClosed()){
                IResponse aResponse = (IResponse) fromServer.readObject();
                this.visit(aResponse);
            }



        }catch (Exception ex){
            out.println("Something went wrong. Disconnecting");
        }finally {
            closeStreams();
        }
    }

    private void closeStreams(){
        try {
            if (fromServer != null){
                fromServer.close();
                fromServer = null;
            }
        }catch(IOException ignored){
            out.println("ObjectInputStream was already closed");
        }

        try{
            if(toServer != null) {
                toServer.close();
                toServer = null;
            }
        } catch(IOException ignored) {
            out.println("ObjectOutputStream was already closed");
        }
    }

    public void disconnect(){
        closeStreams();
    }

    public void send(IRequest aRequest) throws Exception{
        toServer.writeObject(aRequest);
    }

    @Override
    public void handle(InformationResponse aResponse) {
        out.println("List of all available games");
        out.println(aResponse.getGamesAvailable());
    }

    @Override
    public void handle(ParticipationConfirmedResponse aResponse) {
        out.println("Joined the game" + aResponse);
    }

    @Override
    public void handle(CreationGameResponse aResponse) throws Exception {
        out.println("A game should have been created");
    }

    @Override
    public void visit(IResponse aResponse) throws Exception{
        aResponse.accept(this);
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port    = 3000;

        Client aClient = new Client(host, port);
        Thread connectionHandler = new Thread(aClient);
        Thread userOperationHandler = new Thread(aClient::handleUserInput);
        aClient.wait(); 
        try {
            aClient.send(new CreationGameRequest(4));
            connectionHandler.interrupt();
            aClient.disconnect();
        }catch (Exception ex){
            aClient.out.println("error");
        }
    }

    private void handleUserInput() {

    }


}
