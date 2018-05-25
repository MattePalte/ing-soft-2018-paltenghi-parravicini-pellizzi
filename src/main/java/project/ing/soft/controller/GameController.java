package project.ing.soft.controller;

import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.GameFullException;
import project.ing.soft.exceptions.TimeoutOccurredException;
import project.ing.soft.model.Die;
import project.ing.soft.model.Game;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.gamemanager.GameManagerFactory;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.rmi.ViewProxyOverRmi;
import project.ing.soft.view.IView;

import java.io.PrintStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController extends UnicastRemoteObject implements IController, Serializable {


    private transient IGameManager  gameManager;
    private transient Game          theGame;
    private transient PrintStream   logger;

    private transient AtomicBoolean turnEnded;
    private transient Timer         timer;
    private transient TimerTask     timeoutTask;
    private final String id;

    private static final transient long TURN_TIMEOUT       = 60000;
    private static final transient long GAME_START_TIMEOUT = 60000;


    public GameController(int maxNumberOfPlayer, String id) throws RemoteException{
        this.theGame        = new Game(maxNumberOfPlayer);
        this.gameManager    = null;
        this.logger         = new PrintStream(System.out);
        this.turnEnded      = new AtomicBoolean(false);
        this.timer          = new Timer();
        this.id             = id;
    }

    public synchronized int getCurrentPlayers(){
        return theGame.getNumberOfPlayers();
    }

    public synchronized boolean notAlreadyStarted(){
        return this.gameManager == null;
    }

    public synchronized void joinTheGame(String playerName, IView view) throws Exception {

        if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers()) {
            //When the player it's actually instantiated the class is inspected
            //from observations in debugging we observed that the stub object created by rmi
            //is class com.sun.proxy.$Proxy1
            if(view.getClass().getName().contains("sun")) {
                ViewProxyOverRmi proxyOverRmi = new ViewProxyOverRmi(view);
                new Thread(proxyOverRmi).start();
                theGame.add(new Player(playerName, proxyOverRmi));
            }else{
                theGame.add(new Player(playerName, view));
            }

            logger.println( playerName +" added to the match ;)");
        } else {
            throw new GameFullException(" wants to join the game... no space :(");
        }

        if (theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
            startGame();
        }
        /*else{
            timer.schedule(buildStartTimeoutTask(), TURN_TIMEOUT);
        }*/

    }

    @Override
    public synchronized void chooseDie(Die aDie) {
        gameManager.chooseDie(aDie);
    }

    private TimerTask  buildStartTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    startGame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private synchronized void startGame() throws Exception {
        this.gameManager = GameManagerFactory.factory(theGame);
        logger.println( "Setup starting");
        gameManager.setupPhase();
    }

    @Override
    public String getControllerSecurityCode() throws Exception {
        return id;
    }

    @Override
    public synchronized void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {

        Optional<Player> player = gameManager.getPlayerList().stream()
                    .filter((Player p) ->  p.getName().equals(nickname))
                    .findAny();

        if (player.isPresent() ) {
            //bind the player and the pattern
            gameManager.bindPatternAndPlayer(nickname, windowCard, side);
            //if the game is started building a timeout

            if(gameManager.getStatus() == IGameManager.GAME_MANAGER_STATUS.ONGOING) {
                logger.println("match started");
                resetTurnEndAndStartTimer();
            }
        }

    }


    @Override
    public synchronized void requestUpdate() throws Exception{
        gameManager.requestUpdate();
    }

    @Override
    public synchronized void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        if (!gameManager.getCurrentPlayer().getName().equals(nickname))
            throw new ActionNotPermittedException();

        if(turnEnded.get())
            throw new TimeoutOccurredException();

        gameManager.placeDie(aDie, rowIndex, colIndex);

    }

    @Override
    public synchronized void PlayToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if (!gameManager.getCurrentPlayer().getName().equals(nickname))
            throw new ActionNotPermittedException();

        if(turnEnded.get())
            throw new TimeoutOccurredException();

        gameManager.playToolCard(aToolCard);
    }

    //region end turn
    @Override
    public synchronized void endTurn(String nickname) throws Exception {

        if (!gameManager.getCurrentPlayer().getName().equals(nickname))
            throw new ActionNotPermittedException();

        if(turnEnded.get()) {
            throw new TimeoutOccurredException();
        }

        timeoutTask.cancel();
        endTurn(false);

    }

    private synchronized void endTurn(boolean timeoutOccurred) throws Exception {
        gameManager.endTurn(timeoutOccurred);
        resetTurnEndAndStartTimer();

    }
    //endregion

    //region timeout turn

    private void resetTurnEndAndStartTimer(){

        turnEnded.set(false);
        //set true the
        timeoutTask = buildTurnTimeoutTask();
        timer.schedule(timeoutTask, TURN_TIMEOUT);

    }

    private TimerTask buildTurnTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                turnEnded.set(true);
                // if turnEnd == true
                // esci senza far nulla

                try {
                    endTurn(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameController that = (GameController) o;
        return Objects.equals(gameManager, that.gameManager) &&
                Objects.equals(theGame, that.theGame) &&
                Objects.equals(logger, that.logger) &&
                Objects.equals(turnEnded, that.turnEnded) &&
                Objects.equals(timer, that.timer) &&
                Objects.equals(timeoutTask, that.timeoutTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameManager, theGame, turnEnded);
    }

}
