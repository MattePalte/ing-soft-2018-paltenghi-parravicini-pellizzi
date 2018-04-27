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

        new Thread( () -> {
            while(!timeoutExpired){
                if(System.currentTimeMillis() - startTime >= 20000 || theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
                    timeoutExpired = true;
                    this.gameManager = GameManagerFactory.factory(theGame);
                    System.out.println("Setup starting");
                    try {
                        gameManager.setupPhase();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void joinTheGame(String playerName, IView view) throws Exception {
        if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers() && !timeoutExpired) {
            theGame.add(new Player(playerName, view));
            startTime = System.currentTimeMillis();
            logger.log(Level.INFO, "{0} added to the match ;)", playerName);
        } else {
            logger.log(Level.INFO,  "{0} wants to join the game but it is already started :(",playerName);
        }
    }

    @Override
    public void requestUpdate() throws RemoteException{
        gameManager.requestUpdate();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        if (gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.placeDie(aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if (gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.playToolCard(aToolCard);
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        if(gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.endTurn();
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        if (gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.bindPatternAndPlayer(nickname, windowCard, side);
    }
}
