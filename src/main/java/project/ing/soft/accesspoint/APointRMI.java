package project.ing.soft.accesspoint;

import project.ing.soft.Settings;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

public class APointRMI extends UnicastRemoteObject implements IAccessPoint{

    private final HashMap<String, GameController> hostedGameController;

    public APointRMI(HashMap<String,GameController> hostedGameController) throws RemoteException {
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

        GameController gameToJoin = null;
        synchronized (hostedGameController){

            // search controller in already present game but not started
            for (GameController controller : hostedGameController.values()) {
                if (controller.notAlreadyStarted()){
                    gameToJoin = controller;
                }
            }
            // no match avaible for this new user, so create a brand new match only for him
            if (gameToJoin == null){
                String newCode = UUID.randomUUID().toString();
                gameToJoin = new GameController(Settings.nrPlayersOfNewMatch, newCode);
                hostedGameController.put(newCode, gameToJoin);
            }
            try {
                gameToJoin.joinTheGame(nickname, clientView);
            } catch (Exception e) {
                e.printStackTrace();
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
