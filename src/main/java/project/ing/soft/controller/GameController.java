package project.ing.soft.controller;

import project.ing.soft.Settings;
import project.ing.soft.accesspoint.AccessPointReal;
import project.ing.soft.exceptions.*;
import project.ing.soft.model.Die;
import project.ing.soft.model.Game;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.gamemodel.GameModelFactory;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.Player;
import project.ing.soft.view.IView;

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
public class GameController implements IController {

    private  final Game           theGame;
    private  IGameModel           gameModel;
    private  final Logger         log;

    private final AccessPointReal publishingAp;

    private  AtomicBoolean  turnEnded;
    private  Timer          timer;
    private  TimerTask      endTimeoutTask;
    private  TimerTask      startTimeoutTask;


    /**
     * Constructor on a GameController. It also instances a logger to log events and a timer useful
     * to interrupt a player's turn after a certain amount of time
     * @param maxNumberOfPlayer the superior limit of the number of players allowed in the game
     */
    public GameController(int maxNumberOfPlayer, AccessPointReal publishingAp) {
        this.theGame        = new Game(maxNumberOfPlayer);
        this.gameModel      = null;
        this.log            = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Settings.instance().getDefaultLoggingLevel());
        this.turnEnded      = new AtomicBoolean(false);
        this.timer          = new Timer();
        this.endTimeoutTask   = null;
        this.startTimeoutTask = null;

        this.publishingAp   = publishingAp;
    }
    public GameController(int maxNumberOfPlayer) {
        this(maxNumberOfPlayer,null);
    }

    /**
     * @return a boolean flag which indicates whether the game is already started or not by checking
     * if the associated gameModel has already been instanced
     */
    public synchronized boolean notAlreadyStarted(){
        return this.gameModel == null;
    }

    //region connection: available only to objects that knows that this is a LocalController
    /**
     * Method called by a view to connect or reconnect to a game. Obviously if the game is full
     * no other players can join the game. While the game is in the setup phase, if the number of players
     * in the game equals the maximum number of players allowed, then the match starts. If the number of
     * players is bigger than 2 and can't reach the maximum number of players allowed within a certain
     * amount of time, the match starts anyway with only the players already joined.
     * @param playerName name of the player who asked to connect or reconnect to the game
     * @param view player's view reference
     * @throws GameFullException if no other player can be added to the game
     * @throws GameInvalidException if anything went wrong while trying to connect or reconnect the player to the game
     */
    public synchronized void joinTheGame(String playerName, IView view) throws GameFullException, GameInvalidException {
        log.log(Level.INFO,"Add player request received from {0} ", playerName);
        Player player = theGame.getPlayerFromName(playerName);

        if(player != null){
            if(gameModel != null) {
                gameModel.reconnectPlayer(playerName, view);
            }else{
                theGame.reconnect(playerName, view);
            }
        }
        else {
            if (theGame.getNumberOfPlayers() < theGame.getMaxNumPlayers()) {
                theGame.add(new Player(playerName, view));

                log.log(Level.INFO,  "{0} added to the match ;)", playerName);
                if (theGame.getNumberOfPlayers() == theGame.getMaxNumPlayers()) {
                    startTimeoutTask.cancel();
                    internalStartGame();
                }else if(Settings.instance().isGameStartTimeoutEnabled() && theGame.getNumberOfPlayers() == 2) {
                    startTimeoutTask = buildStartTimeoutTask();
                    timer.schedule(startTimeoutTask, Settings.instance().getGameStartTimeout());
                }
            } else {
                throw new GameFullException(playerName+" wants to join the game... no space :(");
            }

        }

    }

    /**
     * Method to mark a player as disconnected. This method is called only by the player's view proxy on the
     * server frontSide if this catches a SocketException or a RemoteException, due to disconnection from
     * socket or RMI communication
     * @param playerName name of the player who disconnected from the game
     */
    public synchronized void markAsDisconnected(String playerName) {
        //if the proper game isn't started we can delete player that
        // get disconnected
        if(gameModel == null) {
            Player p = (Player) theGame.remove(playerName);
            publishingAp.remove(p);
            if(theGame.getNumberOfPlayers() <= 1 && startTimeoutTask != null){
                startTimeoutTask.cancel();
            }
        }else {
            gameModel.disconnectPlayer(playerName);
            if(gameModel.getStatus() == IGameModel.GAME_MANAGER_STATUS.ENDED && publishingAp != null) {
                publishingAp.remove(this);
                TimerTask disconnectPlayersTimeoutTask = buildDisconnectPlayersTimeoutTask();
                timer.schedule(disconnectPlayersTimeoutTask, 60000);
            }
        }
    }
    //endregion

    /**
     * @return a TimerTask to use to disconnect players' thread from ended games only after last events
     * have been sent
     */

    private TimerTask buildDisconnectPlayersTimeoutTask(){
        return new TimerTask() {
            @Override
            public void run() {
                for(Player p : theGame){
                    p.disconnectView();
                }
            }
        };
    }

    /**
     * @return a TimerTask to use in GameController's Timer which makes the match start
     */
    private TimerTask  buildStartTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    internalStartGame();
                } catch (Exception e) {
                    log.log(Level.SEVERE, "error during timer start routine", e);
                }
            }
        };
    }

    /**
     * This method starts the match. It instances the GameModel and launches the setup phase
     * @throws GameInvalidException if anything went wrong during GameModel creation
     */
    private synchronized void internalStartGame() throws GameInvalidException {
        log.log(Level.INFO, "Game started");
        this.gameModel = GameModelFactory.factory(theGame);
        if(gameModel == null) {
            log.log(Level.SEVERE, "GameModelFactory returned a null gameModel!!");
            throw  new GameInvalidException("Game creation problem");
        }
        gameModel.setupPhase();
    }



    //region IController
    /**
     * Method called by players' views to make them choose their window pattern.
     * @param nickname name of the player who chose the pattern
     * @param windowCard pattern card chosen by the player
     * @param side a boolean flag which indicated if the player chose the front or the rear frontSide of the card
     * @throws ActionNotPermittedException if the palyer doesn't appear in the palyer list
     * @throws GameInvalidException if an error occur
     */
    @Override
    public synchronized void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws GameInvalidException, ActionNotPermittedException {

        Player player = theGame.getPlayerFromName(nickname);

        if(player == null){
            log.log(Level.INFO, "Somebody is trying to penetrate our program");
            throw new ActionNotPermittedException();
        }else {
            log.log(Level.INFO, "Player {0} recall choose pattern method", nickname);
            //bind the player and the pattern
            gameModel.bindPatternAndPlayer(nickname, windowCard, side);
            //if the game is started building a timeout

            if(gameModel.getStatus() == IGameModel.GAME_MANAGER_STATUS.ONGOING) {
                log.log(Level.INFO, "Match started");
                resetTurnEndAndStartTimer();
            }
        }

    }

    /**
     * Method used to let players request for a model update to the server
     */
    @Override
    public synchronized void requestUpdate() {
        log.log(Level.INFO, "An model update was requested");
        gameModel.requestUpdate();
    }

    /**
     * Method called by players to place a die in their placedDice matrix
     * @param nickname name of the player who asked to place a die
     * @param aDie the die asked to be placed
     * @param rowIndex of the position in which the die is asked to be placed
     * @param colIndex of the position in which the die is asked to be placed
     * @throws ActionNotPermittedException if you are not the current player
     * @throws TimeoutOccurredException if the action couldn't be performed due to lack of time for the current player
     * @throws PositionOccupiedException if the action couldn't be performed because the position supplied was occupied
     * @throws RuleViolatedException because place die violated a rule
     * @throws PatternConstraintViolatedException because the die couldn't be paced because it doesn't respect the window pattern constrain on (row, col)
     */
    @Override
    public synchronized void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws ActionNotPermittedException, TimeoutOccurredException, PositionOccupiedException, RuleViolatedException, PatternConstraintViolatedException {
        if (!gameModel.getCurrentPlayer().getName().equals(nickname)) {
            log.log(Level.WARNING, "Place die called but player name {0} mismatch ", nickname);
            throw new ActionNotPermittedException();
        }
        if(turnEnded.get()) {
            log.log(Level.INFO, "Player {0} recall place die called but timer was already expired ", nickname);
            throw new TimeoutOccurredException();
        }

        log.log(Level.INFO, "Player {0} recall place die", nickname);
        gameModel.placeDie(aDie, rowIndex, colIndex);

    }

    /**
     * Method called by players to play a toolcard.
     * @param nickname of the player who asked to play a toolcard
     * @param aToolCard Toolcard that the player asked to play
     * @throws ActionNotPermittedException if you are not the current player
     * @throws TimeoutOccurredException if the action couldn't be performed due to lack of time for the current player
     * @throws ToolCardApplicationException if anything went wrong while trying to use the asked toolcard
     * timeout expires
     */
    @Override
    public synchronized void playToolCard(String nickname, ToolCard aToolCard) throws ActionNotPermittedException, TimeoutOccurredException, ToolCardApplicationException {
        if (!gameModel.getCurrentPlayer().getName().equals(nickname)) {
            log.log(Level.WARNING, "Play ToolCard called but player name {0} mismatch ", nickname);
            throw new ActionNotPermittedException();
        }

        if(turnEnded.get()) {
            log.log(Level.INFO, "Player {0} recalled play ToolCard but timer was already expired ", nickname);
            throw new TimeoutOccurredException();
        }
        log.log(Level.INFO, "Player {0} recall play ToolCard", nickname);
        gameModel.playToolCard(aToolCard);
    }
    /**
     * Method called by players to signal they want to end their turn. This method is also automatically
     * called when the timeout expires
     * @param nickname of the player who is taking its turn
     * @throws ActionNotPermittedException f the player who asked to perform operation is not the one who is taking the current turn
     * @throws TimeoutOccurredException if the action couldn't be performed due to lack of time for the current player
     * @throws GameInvalidException if the action corrupted the gameModel
     */
    @Override
    public synchronized void endTurn(String nickname) throws ActionNotPermittedException, TimeoutOccurredException, GameInvalidException {

        if (!gameModel.getCurrentPlayer().getName().equals(nickname)) {
            log.log(Level.WARNING, "End turn called but player name {0} mismatch ", nickname);
            throw new ActionNotPermittedException();
        }

        if(turnEnded.get()) {
            log.log(Level.INFO, "Player {0} recalled end turn but timer was already expired ", nickname);
            throw new TimeoutOccurredException();
        }

        log.log(Level.INFO, "Player {0} recall end turn", nickname);
        endTimeoutTask.cancel();
        internalEndTurn(false);

    }
    //endregion

    //region end turn

    /**
     * Method which end player's turn signaling if the turn is ended by timeout expired or by player's call.
     * After the current turn ended, a new timer is set for the turn which is going to begin
     * @param timeoutOccurred a boolean flag which indicates whether the method has been invoked by a timer
     *                        or by player's call
     * @throws GameInvalidException if anything went wrong while trying to end current turn
     */
    private synchronized void internalEndTurn(boolean timeoutOccurred) throws GameInvalidException {
        log.log(Level.INFO, "End turn {0} ", !timeoutOccurred ? "called" : "forced from timer");
        gameModel.endTurn(timeoutOccurred);
        if(gameModel.getStatus() != IGameModel.GAME_MANAGER_STATUS.ENDED) {
            resetTurnEndAndStartTimer();
        }else if(publishingAp != null) {
            publishingAp.remove(this);
            TimerTask disconnectPlayersTimeoutTask = buildDisconnectPlayersTimeoutTask();
            timer.schedule(disconnectPlayersTimeoutTask, 60000);
        }


    }
    //endregion

    //region timeout turn

    /**
     * Method used to reset the timer for turns timeout
     */
    private void resetTurnEndAndStartTimer(){
        if(Settings.instance().isTurnTimeoutEnabled()) {
            turnEnded.set(false);
            //set true the
            endTimeoutTask = buildTurnTimeoutTask();
            timer.schedule(endTimeoutTask, Settings.instance().getTurnTimeout());
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
                    internalEndTurn(true);
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
        return Objects.equals(gameModel, that.gameModel) &&
                Objects.equals(theGame, that.theGame) &&
                Objects.equals(log, that.log) &&
                Objects.equals(turnEnded, that.turnEnded) &&
                Objects.equals(timer, that.timer) &&
                Objects.equals(endTimeoutTask, that.endTimeoutTask);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}
