package project.ing.soft.controller;

import project.ing.soft.Die;
import project.ing.soft.Game;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.cards.toolcards.ToolCard;
import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.gamemanager.GameManagerFactory;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.view.IView;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller extends UnicastRemoteObject implements IController, Serializable {

    private transient IGameManager gameManager;
    private transient Game         theGame;
    private transient Logger       logger;
    private long startTime;
    private boolean timeoutExpired = false;

    public Controller(int maxNumberOfPlayer) throws RemoteException{
        this.theGame = new Game(maxNumberOfPlayer);
        this.logger  = Logger.getLogger("err");
        this.startTime = System.currentTimeMillis();

        /*new Thread( () -> {
            while(!timeoutExpired){
                if(System.currentTimeMillis() - startTime >= 20000 || theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
                    timeoutExpired = true;
                    this.gameManager = GameManagerFactory.factory(theGame);
                    System.out.println("Setup starting");
                    try {
                        gameManager.setupPhase();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    @Override
    public synchronized void  joinTheGame(String playerName, IView view) throws Exception {
        if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers()) {
            theGame.add(new Player(playerName, view));
            startTime = System.currentTimeMillis();
            logger.log(Level.INFO, "{0} added to the match ;)", playerName);
        } else {
            logger.log(Level.INFO,  "{0} wants to join the game... no space :(",playerName);
        }
        //TODO: provide a timeout to start the game also with less than the max nr of player
        if (theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
            this.gameManager = GameManagerFactory.factory(theGame);
            logger.log(Level.INFO, "Setup starting");
            gameManager.setupPhase();
        }
    }

    @Override
    public synchronized void requestUpdate() throws Exception{
        gameManager.requestUpdate();
    }

    @Override
    public synchronized void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        if (gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.placeDie(aDie, rowIndex, colIndex);
    }

    @Override
    public synchronized void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if (gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.playToolCard(aToolCard);
    }

    @Override
    public synchronized void endTurn(String nickname) throws Exception {
        if(gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.endTurn();
    }

    @Override
    public synchronized void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        Optional<Player> player = gameManager.getPlayerList().stream()
                                             .filter((Player p) ->  p.getName().equals(nickname))
                                             .findAny();
        if (player.isPresent() )
            gameManager.bindPatternAndPlayer(nickname, windowCard, side);
    }
}
