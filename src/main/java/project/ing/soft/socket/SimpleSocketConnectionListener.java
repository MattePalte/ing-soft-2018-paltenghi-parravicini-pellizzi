package project.ing.soft.socket;


import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.request.connectionrequest.ClientConnectionRequestHandler;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class SimpleSocketConnectionListener extends Thread implements IAccessPoint{
    private int     localPort;
    private PrintStream log;
    private Map<String, GameController> hostedGames;
    private Map<String, GameController> playersInGame;
    private ServerSocket aServerSocket;
    private ExecutorService clientAcceptor = Executors.newCachedThreadPool();
    private ExecutorService ex = Executors.newCachedThreadPool();


    public SimpleSocketConnectionListener(int localPort, Map<String, GameController> hostedGames, Map<String, GameController> playersInGame) {
        this.localPort    = localPort;
        this.log = new PrintStream(System.out);
        this.hostedGames = hostedGames;
        this.playersInGame = playersInGame;
    }

    @Override
    public void run(){
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

                clientAcceptor.submit(new ClientConnectionRequestHandler(spilledSocket, this));

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
        HashMap<String, GameController> hostedGames = new HashMap<>();
        HashMap<String, GameController> playersInGame = new HashMap<>();

        SimpleSocketConnectionListener serverThread = new SimpleSocketConnectionListener(3000, hostedGames, playersInGame);
        serverThread.start();


        Scanner input = new Scanner(System.in);

        do{
            System.out.println("If you want to shutdown the server enter q");
        }while(!input.next().startsWith("q") && !serverThread.isInterrupted());

        serverThread.interrupt();
    }

    @Override
    public IController connect(String nickname, IView clientView) throws Exception {
        //when connection is established a game is directly chosen from the list of available ones
        ArrayList<GameController> gamesThatNeedParticipants = hostedGames.values().stream()
                .filter (GameController::notAlreadyStarted)
                .collect(Collectors.toCollection(ArrayList::new));
        GameController selectedGame;

        if (gamesThatNeedParticipants.isEmpty()){
            selectedGame = new GameController(2, UUID.randomUUID().toString());
            hostedGames.put(UUID.randomUUID().toString(), selectedGame);
        }else {
            selectedGame = gamesThatNeedParticipants.get(0);
        }

        //check if the nickname is already taken in the selectedGame
        if(playersInGame.containsKey(nickname))
            throw new NickNameAlreadyTakenException("This nickname can't be used now");
        else {
            //selectedGame.joinTheGame(nickname, clientView);
            playersInGame.put(nickname, selectedGame);
            clientView.attachController(selectedGame);
            ex.submit((ViewProxyOverSocket) clientView);
        }

        return selectedGame;
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws RemoteException {
        return null;
    }
}
