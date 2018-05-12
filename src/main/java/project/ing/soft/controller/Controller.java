package project.ing.soft.controller;

import project.ing.soft.model.Die;
import project.ing.soft.model.Game;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.gamemanager.GameManagerFactory;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.view.IView;

import java.io.PrintStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class Controller extends UnicastRemoteObject implements IController, Serializable {

    private transient IGameManager gameManager;
    private transient Game         theGame;
    private transient PrintStream  logger;
    private long startTime;
    private boolean isGameStarted = false;

    public Controller(int maxNumberOfPlayer) throws RemoteException{
        this.theGame = new Game(maxNumberOfPlayer);
        this.logger  = new PrintStream(System.out);
        this.startTime = System.currentTimeMillis();

        /*new Thread( () -> {
            while(!isGameStarted){
                if(System.currentTimeMillis() - startTime >= 20000 || theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
                    isGameStarted = true;
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

    public int getCurrentPlayers(){
        return theGame.getNumberOfPlayers();
    }

    public boolean getIsStarted(){
        return isGameStarted;
    }

    @Override
    public synchronized void  joinTheGame(String playerName, IView view) throws Exception {
        if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers()) {
            theGame.add(new Player(playerName, view));
            startTime = System.currentTimeMillis();
            logger.println( playerName +" added to the match ;)");
        } else {
            logger.println( playerName + " wants to join the game... no space :(");
            return;
        }
        //TODO: provide a timeout to start the game also with less than the max nr of player
        if (theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
            this.gameManager = GameManagerFactory.factory(theGame);
           logger.println( "Setup starting");
           isGameStarted = true;
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
