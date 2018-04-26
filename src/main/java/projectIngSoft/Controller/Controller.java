package projectIngSoft.Controller;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Die;
import projectIngSoft.Game;
import projectIngSoft.GameManager.GameManagerFactory;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.View.IView;
import projectIngSoft.exceptions.GameInvalidException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Controller extends UnicastRemoteObject implements IController, Serializable {

    private transient IGameManager gameManager;
    private transient Game theGame;
    private long startTime;
    private boolean timeoutExpired = false;

    public Controller(int maxNumberOfPlayer) throws RemoteException{
        this.theGame = new Game(maxNumberOfPlayer);
        this.startTime = System.currentTimeMillis();

        // thread used to provide the controller a timeout. When timeout's expired the game starts with the number of player already added
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
    public void joinTheGame(String plyerName, IView view) throws Exception {
        if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers() && !timeoutExpired) {
            theGame.add(new Player(plyerName, view));
            System.out.println("current start time: " + startTime);
            startTime = System.currentTimeMillis();
            System.out.println("new start time: " + startTime);
            System.out.println(plyerName + " added to the match ;)");
        } else {
            System.out.println(plyerName + " wants to join the game, but it's already started :(");
        }
    }

    @Override
    public void requestUpdate() throws RemoteException{
        gameManager.requestUpdate();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        gameManager.placeDie(aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if (gameManager.getCurrentPlayer().getName().equals(nickname))
            gameManager.playToolCard(aToolCard);
    }

    @Override
    public void endTurn() throws Exception, GameInvalidException {
        gameManager.endTurn();
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception, GameInvalidException {
        gameManager.bindPatternAndPlayer(nickname, windowCard, side);
    }
}
