package project.ing.soft.socket;


import project.ing.soft.Settings;
import project.ing.soft.accesspoint.AccessPointReal;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SocketListener is part of what allows {@link APointSocket} to expose its capabilities
 * to client via {@link APProxySocket}.
 */
public class SocketListener extends Thread {
    private final int localPort;
    private final Logger log;
    private final AccessPointReal accessPointReal;
    private ServerSocket aServerSocket;
    private ExecutorService clientAcceptor;

    /**
     * When a Socket Listener is created it needs
     * @param localPort to accept incoming connection
     * @param accessPointReal that is in charge of admittingplayers to the game
     */
    public SocketListener(int localPort, AccessPointReal accessPointReal) {
        this.localPort      = localPort;
        this.log            = Logger.getLogger(this.getClass().getCanonicalName()+" on port "+localPort);
        this.log.setLevel(Settings.instance().getDefaultLoggingLevel());
        this.accessPointReal = accessPointReal;
        this.clientAcceptor = Executors.newScheduledThreadPool(10);
    }

    /**
     * The main concern of this class is accpeting connection through local socket and passing to {@link APointSocket}
     */
    @Override
    public void run() {
        try {
            aServerSocket = new ServerSocket(localPort);
            log.log(Level.INFO,"Server is up and waiting for connections on port {0}", localPort);
        } catch (IOException ex) {
            log.log(Level.SEVERE,"Probably the port {0} is already used by another program. Terminating", localPort);
            log.log(Level.SEVERE, "Error while opening server-frontSide socket", ex);
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
