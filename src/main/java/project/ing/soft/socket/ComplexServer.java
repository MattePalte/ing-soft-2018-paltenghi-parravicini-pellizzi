package project.ing.soft.socket;


import project.ing.soft.model.Game;
import project.ing.soft.controller.IController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ComplexServer extends Thread {
    private int     localPort;
    private Logger  errLogger ;
    private HashMap<Game, IController> hostedGames;
    private ServerSocket aServerSocket;

    private ComplexServer(int localPort, HashMap hostedGames) {
        this.localPort    = localPort;
        this.errLogger    = Logger.getLogger("error");
        this.hostedGames  = hostedGames;
    }

    @Override
    public void run(){
        ExecutorService ex = Executors.newCachedThreadPool();
        try {
            aServerSocket = new ServerSocket(localPort);
            errLogger.log(Level.INFO, "Server is up and waiting for connections on port {0}", localPort);
        }catch (IOException e1) {
            errLogger.log(Level.INFO, "Probably the port {0} is already used by another program. Terminating", localPort);
            return;
        }


        while(!Thread.currentThread().isInterrupted() && !aServerSocket.isClosed()) {
            try {

                Socket spilledSocket = aServerSocket.accept();
                errLogger.log(Level.INFO,
                        "Server received a connection from {0}", spilledSocket.getRemoteSocketAddress());
                ex.submit(new SocketHandler(spilledSocket, hostedGames));
            } catch (Exception e) {
                errLogger.log(Level.SEVERE, "error was thrown while waiting for clients", e);

            }
        }
        ex.shutdown();
    }

    @Override
    public void interrupt(){
        super.interrupt();
        if(aServerSocket == null )
            return;
        if(aServerSocket.isClosed())
            return;

        try {
            aServerSocket.close();
        }catch (Exception ignored){

        }
    }





    public static void main(String[] args) {
        HashMap<Game, IController> hostedGames = new HashMap<>();

        ComplexServer serverThread = new ComplexServer(3000, hostedGames);
        serverThread.start();


        Scanner input = new Scanner(System.in);

        do{
            System.out.println("If you want to shutdown the server enter q");
        }while(!input.next().startsWith("q") && !serverThread.isInterrupted());

        serverThread.interrupt();
    }
}
