package project.ing.soft.accesspoint;

import project.ing.soft.Settings;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

public class APointRMI extends UnicastRemoteObject implements IAccessPoint{


    private static final String cmdForStartingRegistry = "start rmiregistry.exe -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.useCodebaseOnly=false";
    private static final String classesRootpath =  GameController.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");

    private final HashMap<String, GameController> hostedGameController;

    public APointRMI(HashMap<String,GameController> hostedGameController) throws IOException, InterruptedException {
        this.hostedGameController = hostedGameController;

    }

    public static void export(APointRMI ap) throws IOException, InterruptedException {
        // BEFORE LAUNCHING THE SERVER AND CLIENT
        // launch this script from target/classes folder
        // start rmiregistry -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.useCodebaseOnly=false
        // NB: aggiungi la bin folder di java 9 al path di windows
        // NB: usa la cmd line di windows (no powershell)
        // il game è da due, perciò lancia due client per far inizare una partita in automatico

        // MULTI MACHINE INTERNAL LAN
        // for both the machines
        // change setting of the network to private (allow incoming connection)
        // disable firewall di rete (Window Defender Firewall)
        // check if the machine without the registry can ping the other machine
        // launch this script from target/classes folder
        // start rmiregistry -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.useCodebaseOnly=false -J-Djava.rmi.server.hostname=192.168.x.x
        // where 192.168.x.x is the internal ip of the machine hosting the registry
        // decomment this -> System.setProperty("java.rmi.server.hostname","192.168.x.x");
        System.out.println("Starting rmi registry in "+ classesRootpath);

        //we start registry using another process
        //Notice that the process is actually started into a separate cmd than the one started with this process builder
        // this was done for debugging purposes
        //reference for cmd program
        //https://ss64.com/nt/cmd.html
        //reference for operators in scripting
        //http://mywiki.wooledge.org/BashGuide/TestsAndConditionals
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C "+cmdForStartingRegistry);
        //we set directory starting point
        File classesDir = new File(classesRootpath);
        pb.directory(classesDir);
        //se a log for output
        File log = new File("log.txt");
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
        //start the rmi process
        Process rmiRegistryProcess = pb.start();
        //waiting for server start
        Thread.sleep(1000);
        //it would be nice to test for activity of the thread of rmi registry.
        rmiRegistryProcess.isAlive();
        Registry registry = LocateRegistry.getRegistry();

        // Unbinding old games from registry
        for(String s : registry.list()){
            try {
                registry.unbind(s);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        registry.rebind("accesspoint", ap);
        System.out.println("AccessPoint RMI published on the registry");
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
