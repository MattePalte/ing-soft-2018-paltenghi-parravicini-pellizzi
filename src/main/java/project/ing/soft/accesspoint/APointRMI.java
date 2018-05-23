package project.ing.soft.accesspoint;

import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class APointRMI extends UnicastRemoteObject implements IAccessPoint{

    private final HashMap<String, GameController> hostedGameController;

    protected APointRMI(HashMap<String,GameController> hostedGameController) throws RemoteException {
        this.hostedGameController = hostedGameController;
    }

    /**
     * When a user connect to the server we provide him with a controller
     * chosen from hostedGameController list if it's present, or creating a new
     * GameController and adding it to the list
     * @param nickname
     * @return GameController of a match
     * @throws RemoteException
     */
    @Override
    public IController connect(String nickname, IView clientView) throws RemoteException{
        GameController gameToJoin;
        synchronized (hostedGameController){
            ArrayList<GameController> gamesThatNeedParticipants = hostedGameController.values().stream()
                    .filter (GameController::notAlreadyStarted )
                    .collect(Collectors.toCollection(ArrayList::new));

            if (gamesThatNeedParticipants.isEmpty()) {
                int players = 3;
                gameToJoin = new GameController(players, UUID.randomUUID().toString());
                hostedGameController.put(gameToJoin.getControllerSecurityCode(), gameToJoin);
            } else {
                gameToJoin = gamesThatNeedParticipants.get(0);
            }
            try {
                gameToJoin.joinTheGame(nickname, clientView);
            } catch (Exception e) {
                e.printStackTrace();
                assert (false); // stop the server if exception
            }
            //TODO: create PlayerController and return instead of GameControler
        }
        return gameToJoin;
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws RemoteException {
        GameController gameToReconnect;
        synchronized (hostedGameController) {
            gameToReconnect = hostedGameController.get(code);
        }
        return gameToReconnect;
    }
}
