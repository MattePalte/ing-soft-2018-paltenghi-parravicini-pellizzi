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

    public Controller(int maxNumberOfPlayer) throws RemoteException{
        this.theGame = new Game(maxNumberOfPlayer);
    }

    @Override
    public void joinTheGame(String plyerName, IView view) throws Exception {
        if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers()) {
            theGame.add(new Player(plyerName, view));
            System.out.println(plyerName + " added to the match ;)");
        } else {
            System.out.println(plyerName + " wants to join the game... no space :(");
        }
        //TODO: provide a timeout to start the game also with less than the max nr of player
        if (theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
            this.gameManager = GameManagerFactory.factory(theGame);
            System.out.println("Setup starting");
            gameManager.setupPhase();
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
