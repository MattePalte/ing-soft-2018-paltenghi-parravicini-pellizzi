package project.ing.soft.socket;


import project.ing.soft.controller.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class SimpleSocketConnectionListener extends Thread {
    private int     localPort;
    private PrintStream log;
    private List<Controller> hostedGames;
    private ServerSocket aServerSocket;

    public SimpleSocketConnectionListener(int localPort, List<Controller> hostedGames) {
        this.localPort    = localPort;
        this.log = new PrintStream(System.out);
        this.hostedGames = hostedGames;
    }

    @Override
    public void run(){
        ExecutorService ex = Executors.newCachedThreadPool();
        try {
            aServerSocket = new ServerSocket(localPort);
            log.println( "Server is up and waiting for connections on port "+ localPort);
        }catch (IOException e1) {
            log.println("Probably the port "+localPort+" is already used by another program. Terminating");
            return;
        }


        while(!Thread.currentThread().isInterrupted() && !aServerSocket.isClosed()) {
            try {

                Socket spilledSocket = aServerSocket.accept();
                log.println("Server received a connection from "+ spilledSocket.getRemoteSocketAddress());

                //when connection is established a game is directly chosen from the list of available ones
                ArrayList<Controller> gamesThatNeedParticipants = hostedGames.stream()
                        .filter (Controller::notAlreadyStarted)
                        .collect(Collectors.toCollection(ArrayList::new));
                Controller selectedGame;

                if (gamesThatNeedParticipants.isEmpty()){
                    selectedGame = new Controller(2);
                    hostedGames.add( selectedGame);
                }else {
                    selectedGame = gamesThatNeedParticipants.get(0);


                }

                ViewProxyOverSocket viewProxy = new ViewProxyOverSocket(spilledSocket);
                viewProxy.attachController(selectedGame);
                ex.submit(viewProxy);

            } catch (Exception e) {
                log.println( "error was thrown while waiting for clients");
                e.printStackTrace(log);

            }
        }
        ex.shutdown();
    }

    @Override
    public void interrupt(){
        super.interrupt();
        if(aServerSocket == null || aServerSocket.isClosed())
            return;

        try {
            aServerSocket.close();
        }catch (Exception ignored){
            //This exception should be ignored during shutting down of the system.
        }
    }




    //sample of usage of the class
    //No more than an instance of this class should run in a server.
    public static void main(String[] args) {
        ArrayList<Controller> hostedGames = new ArrayList<>();

        SimpleSocketConnectionListener serverThread = new SimpleSocketConnectionListener(3000, hostedGames);
        serverThread.start();


        Scanner input = new Scanner(System.in);

        do{
            System.out.println("If you want to shutdown the server enter q");
        }while(!input.next().startsWith("q") && !serverThread.isInterrupted());

        serverThread.interrupt();
    }
}
