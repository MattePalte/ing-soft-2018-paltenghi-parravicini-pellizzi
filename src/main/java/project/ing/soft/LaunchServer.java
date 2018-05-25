package project.ing.soft;

import project.ing.soft.accesspoint.APointRMI;
import project.ing.soft.accesspoint.IAccessPoint;
import project.ing.soft.controller.GameController;
import project.ing.soft.socket.SimpleSocketConnectionListener;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import static java.lang.Thread.sleep;

public class LaunchServer extends Thread{

    private final HashMap<String, GameController> hostedGames;
    private final HashMap<String, GameController> playersInGame;

    private static final String cmdForStartingRegistry = "start rmiregistry.exe -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.useCodebaseOnly=false";
    private static final String classesRootpath =  GameController.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");


    public LaunchServer(HashMap<String, GameController> hostedGames, HashMap<String, GameController> playersInGame){
        this.hostedGames = hostedGames;
        this.playersInGame = playersInGame;
    }

    @Override
    public void run() {
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

        Process rmiRegistryProcess = null;
        try{
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
            rmiRegistryProcess = pb.start();
            //waiting for server start
            Thread.sleep(1000);
            //it would be nice to test for ativity of the thread of rmi registry.
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


            IAccessPoint uniqueRmiAP = new APointRMI(hostedGames);

            registry.rebind("accesspoint", uniqueRmiAP);
            System.out.println("AccessPoint RMI published on the registry");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error while starting registry");
            e.printStackTrace();
        }finally {
            if(rmiRegistryProcess != null)
                rmiRegistryProcess.destroy();
        }
    }

    public static void main(String[] args) {
        HashMap<String, GameController> hostedGames = new HashMap<>();
        HashMap<String, GameController> playersInGame = new HashMap<>();
        //Start socket
        SimpleSocketConnectionListener socketConnectionListener = new SimpleSocketConnectionListener(3000, hostedGames, playersInGame);
        socketConnectionListener.start();
        //Start RMI
        LaunchServer rmiConnectionListener = new LaunchServer(hostedGames, playersInGame);
        rmiConnectionListener.start();

        Scanner input = new Scanner(System.in);

        do{
            System.out.println("If you want to shutdown the server enter q");
        }while(!input.next().startsWith("q") );

        socketConnectionListener.interrupt();
        rmiConnectionListener.interrupt();
    }
}
