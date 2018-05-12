package project.ing.soft;

import project.ing.soft.controller.Controller;
import project.ing.soft.controller.IController;
import project.ing.soft.socket.SimpleSocketConnectionListener;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class LaunchServer extends Thread{
    private ArrayList<Controller> hostedGames;
    private ArrayList<Controller> exportedControllers;

    public LaunchServer(ArrayList<Controller> hostedGames){
        this.hostedGames = hostedGames;
        exportedControllers = new ArrayList<>(hostedGames);
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

        try{
            Registry registry = LocateRegistry.getRegistry();

            // Unbinding old games from registry
            for(String s : registry.list()){
                try {
                    registry.unbind(s);
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }

            while(true) {

                // TODO: syncronize this and socket listener on hostedGames
                if(exportedControllers.size()!=hostedGames.size()) {
                    ArrayList<Controller> toAdd = new ArrayList<>(hostedGames);
                    toAdd.removeAll(exportedControllers);
                    int players = 2;
                    toAdd.forEach(game -> {
                        try {
                            registry.rebind("controller" + exportedControllers.size() + 1, game);
                            System.out.println(" >>> Controller Exported");
                            exportedControllers.add(game);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                }

                ArrayList<IController> gamesThatNeedParticipants = hostedGames.stream()
                        .filter(aController -> !aController.getIsStarted())
                        .collect(Collectors.toCollection(ArrayList::new));
                Controller selectedGame;

                if (gamesThatNeedParticipants.isEmpty()) {
                    int players = 2;
                    selectedGame = new Controller(players);
                    hostedGames.add(selectedGame);
                    exportedControllers.add(selectedGame);
                    registry.rebind("controller" + hostedGames.size(), selectedGame);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
        /*while (true) {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(".");
        }*/

    public static void main(String[] args) {
        ArrayList<Controller> hostedGames = new ArrayList();

        SimpleSocketConnectionListener socketConnectionListener = new SimpleSocketConnectionListener(3000, hostedGames);
        socketConnectionListener.start();

        LaunchServer rmiConnectionListener = new LaunchServer(hostedGames);
        rmiConnectionListener.start();

        Scanner input = new Scanner(System.in);

        do{
            System.out.println("If you want to shutdown the server enter q");
        }while(!input.next().startsWith("q"));

        socketConnectionListener.interrupt();
        rmiConnectionListener.interrupt();
    }
}
