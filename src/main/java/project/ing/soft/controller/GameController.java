package project.ing.soft.controller;

import project.ing.soft.Settings;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Main implementation of Controller interface. Every match has an instance of a GameController associated.
 * A GameController exposes methods to a view, to make them call operations on the model without
 * having a direct connection with it. Before delegate the call to the model, the GameController execute
 * some checks about the player who asked to perform operations and makes some preliminary operations,
 * if needed
 */
public class GameController implements IController, Serializable {


    private transient IGameManager  gameManager;
    private transient Game          theGame;
    private transient Logger        log;

    private transient AtomicBoolean turnEnded;
    private transient Timer         timer;
    private transient TimerTask     timeoutTask;
    private final transient String id;


    /**
     * Default constructor on a GameController. It also instances a logger to log events and a timer useful
     * to interrupt a player's turn after a certain amount of time
     * @param maxNumberOfPlayer the superior limit of the number of players allowed in the game
     * @param id string which uniquely identifies a GameController and its associated game
     */
    public GameController(int maxNumberOfPlayer, String id) {
        this.theGame        = new Game(maxNumberOfPlayer);
        this.gameManager    = null;
        this.log            = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Settings.instance().getDefaultLoggingLevel());
        this.turnEnded      = new AtomicBoolean(false);
        this.timer          = new Timer();
        this.id             = id;
    }

    /**
     *
     * @return the number of players playing in the match
     */
    public synchronized int getCurrentPlayers(){
        return theGame.getNumberOfPlayers();
    }

    /**
     *
     * @return a boolean flag which indicates whether the game is already started or not by checking
     * if the associated gameManager has already been instanced
     */
    public synchronized boolean notAlreadyStarted(){
        return this.gameManager == null;
    }

    /**
     * Method called by a view to connect or reconnect to a game. Obviously if the game is full
     * no other players can join the game. While the game is in the setup phase, if the number of players
     * in the game equals the maximum number of players allowed, then the match starts. If the number of
     * players is bigger than 2 and can't reach the maximum number of players allowed within a certain
     * amount of time, the match starts anyway with only the players already joined.
     * @param playerName name of the player who asked to connect or reconnect to the game
     * @param view player's view reference
     * @throws Exception if anything went wrong while trying to connect or reconnect the player to the game
     */
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
            if(Settings.instance().isGAME_START_TIMEOUT_ENABLED() && theGame.getNumberOfPlayers() >= 2)
                timer.schedule(buildStartTimeoutTask(), Settings.instance().getGAME_START_TIMEOUT());
        }

    }

    /**
     * Method to mark a player as disconnected. This method is called only by the player's view proxy on the
     * server side if this catches a SocketException or a RemoteException, due to disconnection from
     * socket or RMI communication
     * @param playerName name of the player who disconnected from the game
     */
    public synchronized void markAsDisconnected(String playerName) {
        if(gameManager != null)
            gameManager.disconnectPlayer(playerName);
    }

    /**
     *
     * @return a TimerTask to use in GameController's Timer which makes the match start
     */
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

    /**
     * This method starts the match. It instances the GameManager and launches the setup phase
     * @throws Exception if anything went wrong during GameManager creation
     */
    private synchronized void startGame() throws Exception {
        log.log(Level.INFO, "Game started");
        this.gameManager = GameManagerFactory.factory(theGame);
        if(gameManager == null) {
            log.log(Level.SEVERE, "GameManagerFactory returned a null gameManager!!");
            throw  new GameInvalidException("Game creation problem");
        }
        gameManager.setupPhase();
    }

    /**
     *
     * @return GameController's identifier string
     */
    public String getControllerSecurityCode() {
        return id;
    }

    /**
     * Method called by players' views to make them choose their window pattern.
     * @param nickname name of the player who chose the pattern
     * @param windowCard pattern card chosen by the player
     * @param side a boolean flag which indicated if the player chose the front or the rear side of the card
     * @throws Exception if anything went wrong while binding player and window pattern
     */
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
                timer.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        resetTurnEndAndStartTimer();
                    }
                }, Settings.instance().getSYNCH_TIME());
            }
        }else{
            log.log(Level.INFO, "Somebody is trying to penetrate our program");
        }

    }


    /**
     * Method used to let players request for a model update to the server
     * @throws Exception if anything went wrong while accepting the request
     */
    public synchronized void requestUpdate() throws Exception{
        log.log(Level.INFO, "An model update was requested");
        gameManager.requestUpdate();
    }

    /**
     * Method called by players to place a die in their placedDice matrix
     * @param nickname name of the player who asked to place a die
     * @param aDie the die asked to be placed
     * @param rowIndex of the position in which the die is asked to be placed
     * @param colIndex of the position in which the die is asked to be placed
     * @throws Exception if anything went wrong while trying to place the die in the asked position or
     * if timeout expires
     */
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

    /**
     * Method called by players to play a toolcard.
     * @param nickname of the player who asked to play a toolcard
     * @param aToolCard Toolcard that the player asked to play
     * @throws Exception if anything went wrong while trying to use the asked toolcard or if
     * timeout expires
     */
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

    /**
     * Method called by players to signal they want to end their turn. This method is also automatically
     * called when the timeout expires
     * @param nickname of the player who is taking its turn
     * @throws Exception if the player who asked to perform operation is not the one who is taking
     * the current turn or if its timeout is already expired
     */
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

    /**
     * Method which end player's turn signaling if the turn is ended by timeout expired or by player's call.
     * After the current turn ended, a new timer is set for the turn which is going to begin
     * @param timeoutOccurred a boolean flag which indicates whether the method has been invoked by a timer
     *                        or by player's call
     * @throws Exception if anything went wrong while trying to end current turn
     */
    private synchronized void endTurn(boolean timeoutOccurred) throws Exception {
        log.log(Level.INFO, "End turn {0} ", timeoutOccurred ? "called" : "forced from timer");
        gameManager.endTurn(timeoutOccurred);
        resetTurnEndAndStartTimer();

    }
    //endregion

    //region timeout turn

    /**
     * Method used to reset the timer for turns timeout
     */
    private void resetTurnEndAndStartTimer(){
        if(Settings.instance().isTURN_TIMEOUT_ENABLED()) {
            turnEnded.set(false);
            //set true the
            timeoutTask = buildTurnTimeoutTask();
            timer.schedule(timeoutTask, Settings.instance().getTURN_TIMEOUT());
        }
    }

    /**
     *
     * @return a TimerTask to use in the Timer which sets boolean flag turnEnded to false and terminates
     * current player's turn
     */
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


}
