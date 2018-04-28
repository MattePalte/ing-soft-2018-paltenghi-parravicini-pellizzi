package project.ing.soft.testsocket;

import project.ing.soft.Game;
import project.ing.soft.testsocket.request.IRequest;
import project.ing.soft.testsocket.request.ParticipationRequest;
import project.ing.soft.testsocket.response.*;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class ComplexClient implements IResponseHandler ,Runnable {
    private int         port;
    private String      host;
    private PrintWriter out;
    private Scanner     in;
    private ObjectInputStream  fromServer;
    private ObjectOutputStream toServer;

    private ReentrantLock lock;
    private Condition timeToJoinTheGame;
    private boolean isUserRegisteredToGame ;

    private ComplexClient(String host, int port){
        this.host = host;
        this.port = port;

        this.lock = new ReentrantLock();
        this.timeToJoinTheGame = lock.newCondition();
        this.isUserRegisteredToGame = false;
    }

    @Override
    public void run() {
        connectAndHandleMessages();
    }

    private void connectAndHandleMessages(){

        Thread userOperationHandler = null;

        try(Socket aSocket = new Socket()) {
            aSocket.connect(new InetSocketAddress(host, port));



            toServer = new ObjectOutputStream(aSocket.getOutputStream());
            toServer.flush();
            fromServer = new ObjectInputStream(aSocket.getInputStream());


            userOperationHandler = new Thread(this::handleUserInput);
            userOperationHandler.start();

            while( !Thread.currentThread().isInterrupted() && !aSocket.isClosed()){
                IResponse aResponse = (IResponse) fromServer.readObject();
                this.visit(aResponse);
            }



        }catch (Exception ex){
            out.println("Something went wrong. Disconnecting");
        }finally {
            closeStreams();
            if(userOperationHandler != null){
                userOperationHandler.interrupt();
            }

        }
    }

    @Override
    public void visit(IResponse aResponse) throws Exception{
        aResponse.accept(this);
    }

    @Override
    public void handle(InformationResponse aResponse) throws Exception{
        out.println("List of all available games");
        out.println(aResponse.getGamesAvailable());

        ArrayList<Game> gamesThatNeedParticipants = aResponse.getGamesAvailable().stream()
                .filter( aGame -> aGame.getMaxNumPlayers() != aGame.getNumberOfPlayers())
                .collect(Collectors.toCollection(ArrayList::new));

        if( !gamesThatNeedParticipants.isEmpty()){
            if(in.hasNext())
                in.next();
            out.println("There are games which are seeking for players do you want to join one of them? [y/n]");
            if(in.next().startsWith("y")){
                send(new ParticipationRequest(gamesThatNeedParticipants.get(0)));
            }

        }
    }

    @Override
    public void handle(ParticipationConfirmedResponse aResponse) {

        out.println("Joined the game" + aResponse);
        lock.lock();
        isUserRegisteredToGame = true;
        timeToJoinTheGame.signalAll();
        lock.unlock();
    }

    @Override
    public void handle(CreationGameResponse aResponse) throws Exception {

        out.println("A game should have been created");
    }

    @Override
    public void handle(ExceptionalResponse aResponse) {
        out.println("An erroneous response was received");
    }

    @Override
    public void handle(EventResponse aResponse) throws Exception {
        out.println("An erroneous response was received");
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

    public Condition getCondition(){
        return timeToJoinTheGame;
    }

    public Lock getLock(){
        return lock;
    }

    public boolean isUserRegisteredToGame() {
        return isUserRegisteredToGame;
    }

    public synchronized void send(IRequest aRequest) throws Exception{
        toServer.writeObject(aRequest);
    }




    public static void main(String[] args) {
        String host = "localhost";
        int port    = 3000;

        ComplexClient aClient = new ComplexClient(host, port);
        Thread connectionHandler = new Thread(aClient);
        connectionHandler.start();

        aClient.getLock().lock();
        try {
            while (!aClient.isUserRegisteredToGame()) {
                aClient.getCondition().await();
            }

            connectionHandler.interrupt();


            aClient.disconnect();
            ControllerProxy controller = new ControllerProxy(host, port);
            System.out.println("ready for next step");
        }catch (InterruptedException ex){

        }finally {
            aClient.getLock().unlock();
        }



    }

    private void handleUserInput() {

        out.println("Hi man, we're connected to the server");
        /*while(!Thread.currentThread().isInterrupted()){



        }*/

    }


}
