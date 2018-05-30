package project.ing.soft.accesspoint;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SocketListener extends Thread {
    private final int localPort;
    private final Logger log;
    private final AccessPointReal accessPointReal;
    private ServerSocket aServerSocket;
    private ExecutorService clientAcceptor = Executors.newCachedThreadPool();

    public SocketListener(int localPort, AccessPointReal accessPointReal) {
        this.localPort      = localPort;
        this.log            = Logger.getLogger(this.getClass().getCanonicalName()+" on port "+localPort);
        this.log.setLevel(Level.OFF);
        this.accessPointReal = accessPointReal;
    }

    @Override
    public void run() {
        try {
            aServerSocket = new ServerSocket(localPort);
            log.log(Level.INFO,"Server is up and waiting for connections on port {0}", localPort);
        } catch (IOException ex) {
            log.log(Level.SEVERE,"Probably the port {0} is already used by another program. Terminating", localPort);
            log.log(Level.SEVERE, "Error while opening server-side socket", ex);
            return;
        }


        while (!Thread.currentThread().isInterrupted() && !aServerSocket.isClosed()) {
            try {

                Socket spilledSocket = aServerSocket.accept();
                log.log(Level.INFO,"Server received a connection from {0}", spilledSocket.getRemoteSocketAddress());

                clientAcceptor.submit(new APointSocket(spilledSocket, accessPointReal));

            } catch (Exception e) {
                log.log(Level.INFO,"error was thrown while waiting for clients",e );
            }
        }
        clientAcceptor.shutdown();
    }

    @Override
    public void interrupt() {
        log.log(Level.SEVERE, "interrupting socket AP");
        if (aServerSocket == null || aServerSocket.isClosed())
            return;

        try {
            aServerSocket.close();
        } catch (Exception ignored) {
            //This exception should be ignored during shutting down of the system.
        }
        super.interrupt();
    }


}
