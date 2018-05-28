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
import java.util.stream.Collectors;


public class APSocket extends Thread implements IAccessPoint {
    private int localPort;
    private PrintStream log;
    private final Map<String, GameController> hostedGames;
    private final Map<String, GameController> playersInGame;
    private ServerSocket aServerSocket;
    private ExecutorService clientAcceptor = Executors.newCachedThreadPool();

    public APSocket(int localPort, Map<String, GameController> hostedGames, Map<String, GameController> playersInGame) {
        this.localPort = localPort;
        this.log = new PrintStream(System.out);
        this.hostedGames = hostedGames;
        this.playersInGame = playersInGame;
    }

    @Override
    public void run() {
        try {
            aServerSocket = new ServerSocket(localPort);
            log.println("Server is up and waiting for connections on port " + localPort);
        } catch (IOException e1) {
            log.println("Probably the port " + localPort + " is already used by another program. Terminating");
            return;
        }


        while (!Thread.currentThread().isInterrupted() && !aServerSocket.isClosed()) {
            try {

                Socket spilledSocket = aServerSocket.accept();
                log.println("Server received a connection from " + spilledSocket.getRemoteSocketAddress());

                clientAcceptor.submit(new ClientConnectionRequestHandler(spilledSocket, this));

            } catch (Exception e) {
                log.println("error was thrown while waiting for clients");
                e.printStackTrace(log);

            }
        }
        clientAcceptor.shutdown();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (aServerSocket == null || aServerSocket.isClosed())
            return;

        try {
            aServerSocket.close();
        } catch (Exception ignored) {
            //This exception should be ignored during shutting down of the system.
        }
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

    @Override
    public IController connect(String nickname, IView clientView) throws Exception {
        GameController gameToJoin = null;
        synchronized (hostedGames) {
            //when connection is established a game is directly chosen from the list of available ones
            ArrayList<GameController> gamesThatNeedParticipants = hostedGames.values().stream()
                    .filter(GameController::notAlreadyStarted)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (gamesThatNeedParticipants.isEmpty()) {
                gameToJoin = new GameController(Settings.instance().getNrPlayersOfNewMatch(), UUID.randomUUID().toString());
                hostedGames.put(UUID.randomUUID().toString(), gameToJoin);
            } else {
                gameToJoin = gamesThatNeedParticipants.get(0);
            }

            //check if the nickname is already taken in the selectedGame
            if (playersInGame.containsKey(nickname))
                throw new NickNameAlreadyTakenException("This nickname can't be used now");
            else {
                //selectedGame.joinTheGame(nickname, clientView);
                playersInGame.put(nickname, gameToJoin);
                clientView.attachController(gameToJoin);
                ((Thread)clientView).start();
            }
        }
        return gameToJoin;
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws Exception {
        if(!playersInGame.containsKey(nickname))
            throw new ActionNotPermittedException("This nickname does not exist in any game");
        GameController gameToJoin = playersInGame.get(nickname);
        if (code.length() != 32)
            throw new CodeInvalidException("The code must be 32 characters long");
        if(!checkToken(nickname, code))
            throw new CodeInvalidException("The code you have inserted is not valid");
        clientView.attachController(gameToJoin);
        ((Thread)clientView).start();
        return gameToJoin;
    }

    private boolean checkToken(String nickname, String code) throws Exception {
        String gameUUID = playersInGame.get(nickname).getControllerSecurityCode();
        String computedCode = TokenCalculator.computeDigest(nickname + gameUUID);
        return code.equals(computedCode);
    }
}
