package project.ing.soft.accesspoint;

import project.ing.soft.Settings;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.rmi.ViewProxyOverRmi;
import project.ing.soft.view.IView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class APointRMI extends UnicastRemoteObject implements IAccessPoint{

    private static final String ACCESSPOINT = "accesspoint";
    private final transient Logger log;
    private final AccessPointReal accessPointReal;
    private static final String CMD_FOR_STARTING_REGISTRY = "start rmiregistry.exe -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.useCodebaseOnly=false";
    private static final String CLASSES_ROOTPATH =  GameController.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");

    public APointRMI(AccessPointReal accessPointReal) throws RemoteException {
        super();
        this.accessPointReal = accessPointReal;
        this.log = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Level.OFF);
    }

    public static void bind(APointRMI ap) throws IOException, InterruptedException {
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
        ap.log.log(Level.INFO,"Starting rmi registry in {0}", CLASSES_ROOTPATH);

        //we start registry using another process
        //Notice that the process is actually started into a separate cmd than the one started with this process builder
        // this was done for debugging purposes
        //reference for cmd program
        //https://ss64.com/nt/cmd.html
        //reference for operators in scripting
        //http://mywiki.wooledge.org/BashGuide/TestsAndConditionals
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C "+ CMD_FOR_STARTING_REGISTRY);
        //we set directory starting point
        File classesDir = new File(CLASSES_ROOTPATH);
        pb.directory(classesDir);
        //se a log for output
        File log = new File("log.txt");
        pb.redirectErrorStream(true);
        pb.redirectOutput( ProcessBuilder.Redirect.appendTo(log));

        //start the rmi process
        Process rmiRegistryProcess = pb.start();
        //waiting for server start
        Thread.sleep(5000);
        //it would be nice to test for activity of the thread of rmi registry.
        rmiRegistryProcess.isAlive();
        Registry registry = LocateRegistry.getRegistry();
        // Unbinding old ap from registry
        for(String s : registry.list()){
            try {
                registry.unbind(s);
            } catch (NotBoundException e) {
                ap.log.log(Level.SEVERE,"error while unbinding already present element in registry", e);
            }
        }
        registry.rebind(ACCESSPOINT, ap);
        ap.log.log(Level.INFO,"AccessPoint RMI published on the registry");
    }

    public static void unbind(APointRMI ap) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        registry.unbind(ACCESSPOINT);
        ap.log.log(Level.INFO,"AccessPoint RMI removed from the registry");
    }

    /**
     * When a user connect to the server we provide him with a controller
     * chosen from hostedGameController list if it's present, or creating a new
     * GameController and adding it to the list
     * @param nickname of the player
     * @return GameController of a match
     * @throws RemoteException if action does not end well.
     */
    @Override
    public IController connect(String nickname, IView clientView) throws Exception{
        log.log(Level.INFO,"{0} request to connect", nickname);
        ViewProxyOverRmi proxyOverRmi = new ViewProxyOverRmi(clientView, nickname);
        IController newController = accessPointReal.connect(nickname, proxyOverRmi);
        // POST CONNECT ->
        proxyOverRmi.start();
        return newController;
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws Exception {
        log.log(Level.INFO,"{0} requested to reconnect", nickname);
        IController gameToReconnect = accessPointReal.reconnect(nickname, code, clientView);
        return gameToReconnect;
    }

}
