package project.ing.soft.accesspoint;

import project.ing.soft.Settings;
import project.ing.soft.TokenCalculator;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.model.gamemanager.events.SetTokenEvent;
import project.ing.soft.view.IView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AccessPointReal implements IAccessPoint {

    private final Logger log;
    private final Map<String, GameController> hostedGames;
    private final Map<String, GameController> playersInGame;

    public AccessPointReal(Map<String, GameController> hostedGames, Map<String, GameController> playersInGame) {
        this.hostedGames = hostedGames;
        this.playersInGame = playersInGame;
        this.log = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Level.OFF);
    }

    @Override
    public synchronized IController connect(String nickname, IView clientView) throws Exception {
        log.log(Level.INFO,"{0} request to connect", nickname);
        GameController gameControllerToJoin = null;
        //when connection is established a game is directly chosen from the list of available ones
        ArrayList<GameController> gamesThatNeedParticipants = hostedGames.values().stream()
                .filter(GameController::notAlreadyStarted)
                .collect(Collectors.toCollection(ArrayList::new));

        if (gamesThatNeedParticipants.isEmpty()) {
            gameControllerToJoin = new GameController(Settings.instance().getNrPlayersOfNewMatch(), UUID.randomUUID().toString());
            hostedGames.put(UUID.randomUUID().toString(), gameControllerToJoin);
            log.log(Level.INFO,"No game looking for player was found. A new game was created");
        } else {
            gameControllerToJoin = gamesThatNeedParticipants.get(0);
        }

        //check if the nickname is already taken in the selectedGame
        if (playersInGame.containsKey(nickname)) {
            log.log(Level.INFO,"{0} is already used in the game connecting to", nickname);
            throw new NickNameAlreadyTakenException("This nickname can't be used now");
        }else {
            playersInGame.put(nickname, gameControllerToJoin);
            clientView.attachController(gameControllerToJoin);
        }
        log.log(Level.INFO,"{0} connection request succeed", nickname);


        String token = TokenCalculator.computeDigest(nickname + gameControllerToJoin.getControllerSecurityCode());
        clientView.update(new SetTokenEvent(token));
        log.log(Level.INFO, "Associated ({0}, {1}) token: {2}", new Object[]{nickname, gameControllerToJoin.getControllerSecurityCode(), token});
        gameControllerToJoin.joinTheGame(nickname, clientView);
        return gameControllerToJoin;
    }

    @Override
    public synchronized IController reconnect(String nickname, String code, IView clientView) throws Exception {
        log.log(Level.INFO,"{0} requested to reconnect", nickname);
        if(!playersInGame.containsKey(nickname)) {
            log.log(Level.SEVERE,"{0} nickname does not exist in any game", nickname);
            throw new ActionNotPermittedException("This nickname does not exist in any game");
        }
        GameController gameControllerToJoin = playersInGame.get(nickname);
        if (code.length() != 32){
            log.log(Level.SEVERE,"{0} code length mismatch", nickname);
            throw new CodeInvalidException("The code must be 32 characters long");
        }
        if(!checkToken(nickname, code)) {
            log.log(Level.SEVERE,"{0} code {1} did not pass code check", new Object[]{nickname, code});
            throw new CodeInvalidException("The code you have inserted is not valid");
        }
        clientView.attachController(gameControllerToJoin);

        log.log(Level.INFO,"{0} reconnection request succeed", nickname);
        gameControllerToJoin.joinTheGame(nickname,clientView);
        return gameControllerToJoin;
    }

    private boolean checkToken(String nickname, String code) throws Exception {
        String gameUUID = playersInGame.get(nickname).getControllerSecurityCode();
        String computedCode = TokenCalculator.computeDigest(nickname + gameUUID);
        return code.equals(computedCode);
    }
}
