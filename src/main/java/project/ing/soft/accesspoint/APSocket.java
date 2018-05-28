package project.ing.soft.accesspoint;


import project.ing.soft.Settings;
import project.ing.soft.TokenCalculator;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.request.connectionrequest.ClientConnectionRequestHandler;
import project.ing.soft.view.IView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class APSocket extends Thread implements IAccessPoint {
    private int localPort;
    private final Logger log;
    private final Map<String, GameController> hostedGames;
    private final Map<String, GameController> playersInGame;
    private ServerSocket aServerSocket;
    private ExecutorService clientAcceptor = Executors.newCachedThreadPool();

    public APSocket(int localPort, Map<String, GameController> hostedGames, Map<String, GameController> playersInGame) {
        this.localPort      = localPort;
        this.log            = Logger.getLogger(this.getClass().getCanonicalName()+" on port "+localPort);
        this.hostedGames    = hostedGames;
        this.playersInGame  = playersInGame;
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

                clientAcceptor.submit(new ClientConnectionRequestHandler(spilledSocket, this));

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




    @Override
    public IController connect(String nickname, IView clientView) throws Exception {
        log.log(Level.INFO,"{0} request to connect", nickname);
        GameController gameToJoin = null;
        synchronized (hostedGames) {
            //when connection is established a game is directly chosen from the list of available ones
            ArrayList<GameController> gamesThatNeedParticipants = hostedGames.values().stream()
                    .filter(GameController::notAlreadyStarted)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (gamesThatNeedParticipants.isEmpty()) {
                gameToJoin = new GameController(Settings.instance().getNrPlayersOfNewMatch(), UUID.randomUUID().toString());
                hostedGames.put(UUID.randomUUID().toString(), gameToJoin);
                log.log(Level.INFO,"No game looking for player was found. A new game was created");
            } else {
                gameToJoin = gamesThatNeedParticipants.get(0);
            }

            //check if the nickname is already taken in the selectedGame
            if (playersInGame.containsKey(nickname)) {
                log.log(Level.INFO,"{0} is already used in the game connecting to", nickname);
                throw new NickNameAlreadyTakenException("This nickname can't be used now");
            }else {
                //selectedGame.joinTheGame(nickname, clientView);
                playersInGame.put(nickname, gameToJoin);
                clientView.attachController(gameToJoin);
                ((Thread)clientView).start();

            }
        }
        log.log(Level.INFO,"{0} connection request succeed", nickname);
        return gameToJoin;
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws Exception {
        log.log(Level.INFO,"{0} requested to reconnect", nickname);
        if(!playersInGame.containsKey(nickname)) {
            log.log(Level.SEVERE,"{0} nickname does not exist in any game", nickname);
            throw new ActionNotPermittedException("This nickname does not exist in any game");
        }
        GameController gameToJoin = playersInGame.get(nickname);
        if (code.length() != 32){
            log.log(Level.SEVERE,"{0} code length mismatch", nickname);
            throw new CodeInvalidException("The code must be 32 characters long");
        }
        if(!checkToken(nickname, code)) {
            log.log(Level.SEVERE,"{0} code {1} did not pass code check", new Object[]{nickname, code});
            throw new CodeInvalidException("The code you have inserted is not valid");
        }
        clientView.attachController(gameToJoin);
        ((Thread)clientView).start();
        log.log(Level.INFO,"{0} reconnection request succeed", nickname);
        return gameToJoin;
    }

    private boolean checkToken(String nickname, String code) throws Exception {
        String gameUUID = playersInGame.get(nickname).getControllerSecurityCode();
        String computedCode = TokenCalculator.computeDigest(nickname + gameUUID);
        return code.equals(computedCode);
    }

    //sample of usage of the class
    //No more than an instance of this class should run in a server.
    public static void main(String[] args) {
        HashMap<String, GameController> hostedGames = new HashMap<>();
        HashMap<String, GameController> playersInGame = new HashMap<>();

        APSocket serverThread = new APSocket(Settings.instance().getPort(), hostedGames, playersInGame);
        serverThread.start();


        Scanner input = new Scanner(System.in);

        do {
            System.out.println("If you want to shutdown the server enter q");
        } while (!input.next().startsWith("q") && !serverThread.isInterrupted());

        serverThread.interrupt();
    }
}
