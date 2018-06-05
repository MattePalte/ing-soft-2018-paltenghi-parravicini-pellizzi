package project.ing.soft.controller;

import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.GameFullException;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.exceptions.TimeoutOccurredException;
import project.ing.soft.model.Die;
import project.ing.soft.model.Game;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.gamemanager.GameManagerFactory;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.view.IView;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.rmi.server.Unreferenced;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameController extends UnicastRemoteObject implements IController, Serializable, Unreferenced {


    private transient IGameManager  gameManager;
    private transient Game          theGame;
    private transient Logger        log;

    private transient AtomicBoolean turnEnded;
    private transient Timer         timer;
    private transient TimerTask     timeoutTask;
    private final transient String id;

    private static final transient long TURN_TIMEOUT       = 120000;
    private static final transient long GAME_START_TIMEOUT = 60000;


    public GameController(int maxNumberOfPlayer, String id) throws RemoteException {
        super();
        this.theGame        = new Game(maxNumberOfPlayer);
        this.gameManager    = null;
        this.log            = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Level.OFF);
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
        log.log(Level.INFO,"Add player request received from {0} ", playerName);
        Optional<Player> player = theGame.getPlayers()
                .stream()
                .filter(p->p.getName().equals(playerName))
                .findFirst();

        if(player.isPresent()){

            if(gameManager != null && gameManager.getStatus() != IGameManager.GAME_MANAGER_STATUS.ENDED) {
                theGame.reconnect(playerName, view);
                gameManager.reconnectPlayer(playerName, view);
            }
            else{
                theGame.reconnect(playerName, view);
            }
        }
        else {
            if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers()) {
                theGame.add(new Player(playerName, view));

                log.log(Level.INFO,  "{0} added to the match ;)", playerName);
            } else {
                throw new GameFullException(" wants to join the game... no space :(");
            }

            if (theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
                startGame();
            }
        }
        /*else{
            timer.schedule(buildStartTimeoutTask(), TURN_TIMEOUT);
        }*/

    }

    public synchronized void markAsDisconnected(String playerName) {
        if(gameManager != null)
            gameManager.disconnectPlayer(playerName);
    }

    private TimerTask  buildStartTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    startGame();
                } catch (Exception e) {
                    log.log(Level.SEVERE, "error during timer start routine", e);
                }
            }
        };
    }

    private synchronized void startGame() throws Exception {
        log.log(Level.INFO, "Game started");
        this.gameManager = GameManagerFactory.factory(theGame);
        if(gameManager == null) {
            log.log(Level.SEVERE, "GameManagerFactory returned a null gameManager!!");
            throw  new GameInvalidException("Game creation problem");
        }
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
            log.log(Level.INFO, "Player {0} recall choose pattern method", nickname);
            //bind the player and the pattern
            gameManager.bindPatternAndPlayer(nickname, windowCard, side);
            //if the game is started building a timeout

            if(gameManager.getStatus() == IGameManager.GAME_MANAGER_STATUS.ONGOING) {
                log.log(Level.INFO, "Match started");
                resetTurnEndAndStartTimer();
            }
        }else{
            log.log(Level.INFO, "Somebody is trying to penetrate our program");
        }

    }


    @Override
    public synchronized void requestUpdate() throws Exception{
        log.log(Level.INFO, "An model update was requested");
        gameManager.requestUpdate();
    }

    @Override
    public synchronized void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        if (!gameManager.getCurrentPlayer().getName().equals(nickname)) {
            log.log(Level.WARNING, "Place die called but player name {0} mismatch ", nickname);
            throw new ActionNotPermittedException();
        }
        if(turnEnded.get()) {
            log.log(Level.INFO, "Player {0} recall place die called but timer was already expired ", nickname);
            throw new TimeoutOccurredException();
        }

        log.log(Level.INFO, "Player {0} recall place die", nickname);
        gameManager.placeDie(aDie, rowIndex, colIndex);

    }

    @Override
    public synchronized void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if (!gameManager.getCurrentPlayer().getName().equals(nickname)) {
            log.log(Level.WARNING, "Play ToolCard called but player name {0} mismatch ", nickname);
            throw new ActionNotPermittedException();
        }

        if(turnEnded.get()) {
            log.log(Level.INFO, "Player {0} recalled play ToolCard but timer was already expired ", nickname);
            throw new TimeoutOccurredException();
        }
        log.log(Level.INFO, "Player {0} recall play ToolCard", nickname);
        gameManager.playToolCard(aToolCard);
    }

    //region end turn
    @Override
    public synchronized void endTurn(String nickname) throws Exception {

        if (!gameManager.getCurrentPlayer().getName().equals(nickname)) {
            log.log(Level.WARNING, "End turn called but player name {0} mismatch ", nickname);
            throw new ActionNotPermittedException();
        }

        if(turnEnded.get()) {
            log.log(Level.INFO, "Player {0} recalled end turn but timer was already expired ", nickname);
            throw new TimeoutOccurredException();
        }

        log.log(Level.INFO, "Player {0} recall end turn", nickname);
        timeoutTask.cancel();
        endTurn(false);

    }

    private synchronized void endTurn(boolean timeoutOccurred) throws Exception {
        log.log(Level.INFO, "End turn {0} ", timeoutOccurred ? "called" : "forced from timer");
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

                try {
                    endTurn(true);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Error while resetting turn by timer action", e);
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
                Objects.equals(log, that.log) &&
                Objects.equals(turnEnded, that.turnEnded) &&
                Objects.equals(timer, that.timer) &&
                Objects.equals(timeoutTask, that.timeoutTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameManager, theGame, turnEnded);
    }

    @Override
    public void unreferenced() {
        System.out.println("unreferenced!");
    }
}
