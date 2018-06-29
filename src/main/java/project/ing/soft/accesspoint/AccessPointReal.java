package project.ing.soft.accesspoint;

import project.ing.soft.Settings;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.*;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemodel.events.SetTokenEvent;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class is the real implementation of AccessPoint, every type of connection
 * (both RMI and socket) communicates with this object. This object is the only one
 * that can have the entire set of hosted games.
 * Its task is handling the exposure of the games to the external wolrd.
 * Its main concerns are related to:
 * - joining player and games waiting for them;
 * - connect players that lose connection to the match they were attending;
 * - remove games that does not need connection facilities any more.
 */
public class AccessPointReal implements IAccessPoint {

    private final Logger log;
    private final Map<GameController, String> hostedGamesIds;
    private final Map<String, GameController> playersInGame;


    public AccessPointReal() {
        this.hostedGamesIds = new HashMap<>();
        this.playersInGame  = new HashMap<>();
        this.log = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Settings.instance().getDefaultLoggingLevel());
    }

    /**
     * method to get access for the first time to a gameController
     * @param nickname the nickname chosen by the client for that game
     * @param clientView the view object representing the client
     * @return the controller that is able to handle the request for that player
     * @throws NickNameAlreadyTakenException if the nickname is already used by another gentleman to play sagrada
     * @throws GameInvalidException if an error occurred while joining the player in the game
     */
    @Override
    public synchronized IController connect(String nickname, IView clientView) throws NickNameAlreadyTakenException, GameInvalidException {
        log.log(Level.INFO,"{0} request to connect", nickname);
        GameController gameControllerToJoin ;
        //First of all check if the nickname is already taken in the selectedGame
        if (playersInGame.containsKey(nickname)) {
            log.log(Level.INFO,"{0} is already used in the game connecting to", nickname);
            throw new NickNameAlreadyTakenException("This nickname can't be used now");
        }
        //when connection is established a game is directly chosen from the list of available ones
        ArrayList<GameController> gamesThatNeedParticipants = hostedGamesIds.keySet().stream()
                .filter(GameController::notAlreadyStarted)
                .collect(Collectors.toCollection(ArrayList::new));

        if (gamesThatNeedParticipants.isEmpty()) {
            gameControllerToJoin = new GameController(Settings.instance().getNrPlayersOfNewMatch(), this);
            hostedGamesIds.put(gameControllerToJoin,UUID.randomUUID().toString() );
            log.log(Level.INFO,"No game looking for player was found. A new game was created");
        } else {
            gameControllerToJoin = gamesThatNeedParticipants.get(0);
        }

        playersInGame.put(nickname, gameControllerToJoin);
        try {
            clientView.attachController(gameControllerToJoin);
            String token = TokenCalculator.computeDigest(nickname + hostedGamesIds.get(gameControllerToJoin));
            clientView.update(new SetTokenEvent(token));
            log.log(Level.INFO, "Connection succeed: Associated ({0}, {1}) token: {2}", new Object[]{nickname, gameControllerToJoin, token});
            gameControllerToJoin.joinTheGame(nickname, clientView);
        } catch (GameFullException | IOException e) {
            //since we have checked both that:
            //  1. the view supplied as a parameter is on the server
            //  2. the game can support another player
            assert false;
        }
        return gameControllerToJoin;
    }

    /**
     * Real implementation of {@link IAccessPoint#reconnect(String, String, IView)}
     * @param nickname the nickname used by the client in the game when he lost the connection
     * @param code a code to help the server to connect the client to the right game
     * @param clientView the view object representing the client
     * @return the controller that is able to handle the request for that player
     * @throws ActionNotPermittedException if the player does not seems even to be conneted to this access point
     * @throws CodeInvalidException if the code supplied as an argument doesn't appear to be correct
     * @throws GameInvalidException if an error was raised by the model during recconect operations
     */
    @Override
    public synchronized IController reconnect(String nickname, String code, IView clientView) throws ActionNotPermittedException, CodeInvalidException, GameInvalidException {
        log.log(Level.INFO,"{0} requested to reconnect", nickname);
        if(!playersInGame.containsKey(nickname)) {
            log.log(Level.SEVERE,"{0} nickname does not exist in any game", nickname);
            throw new ActionNotPermittedException("This nickname does not exist in any game");
        }else if (code.length() != 32){
            log.log(Level.SEVERE,"{0} code length mismatch", nickname);
            throw new CodeInvalidException("The code must be 32 characters long");
        }else if(!checkToken(nickname, code)) {
            log.log(Level.SEVERE,"{0} code {1} did not pass code check", new Object[]{nickname, code});
            throw new CodeInvalidException("The code you have inserted is not valid");
        }

        GameController gameControllerToJoin = playersInGame.get(nickname);
        try {
            clientView.attachController(gameControllerToJoin);
            String token = TokenCalculator.computeDigest(nickname + hostedGamesIds.get(gameControllerToJoin));
            clientView.update(new SetTokenEvent(token));
            log.log(Level.INFO, "Associated ({0}, {1}) token: {2}", new Object[]{nickname, gameControllerToJoin, token});
            gameControllerToJoin.joinTheGame(nickname,clientView);
            log.log(Level.INFO,"{0} reconnection request succeed", nickname);
        } catch (GameFullException | IOException e) {
            //since we have checked both that:
            //  1. the view supplied as a parameter is on the server
            //  2. the game can support another player
            assert false;
        }
        return gameControllerToJoin;
    }

    /**
     * The method test the validity of the supplied code
     * @param nickname nickname of the player to be tested
     * @param code the code supplied by the player at the connection/reconnection
     * @return true if the player is considered admittable
     */
    private boolean checkToken(String nickname, String code) {
        String gameUUID = hostedGamesIds.get(playersInGame.get(nickname));
        String computedCode = TokenCalculator.computeDigest(nickname + gameUUID);
        return code.equals(computedCode);
    }

    /**
     * Entry point to delete a controller that previously has been created by this AccessPoint
     * @param aCompletedGameController the GameController that has to be removed.
     */
    public synchronized void remove(GameController aCompletedGameController) {
        log.log(Level.INFO, "A game controller {0} request a remove", aCompletedGameController);
        this.hostedGamesIds.remove(aCompletedGameController);
        this.playersInGame.values().removeIf(aContr->aContr == aCompletedGameController);
    }

    /**
     * Entry point to delete a player that previously has been created by this AccessPoint
     * @param player that has to be removed from a not started game
     */
    public synchronized void remove(Player player) {
        log.log(Level.INFO, "The player {0} was request to be removed", player);
        this.playersInGame.keySet().removeIf(name-> name.equals(player.getName()));
    }
}
