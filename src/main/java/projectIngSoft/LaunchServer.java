package projectIngSoft;

import projectIngSoft.Controller.Controller;
import projectIngSoft.Controller.IController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.Thread.sleep;

public class LaunchServer {
    public static void main(String[] args) throws RemoteException {
        // BEFORE LAUNCHING THE SERVER AND CLIENT
        // launch this script from target/classes folder
        // start rmiregistry -J-Djava.rmi.server.logCalls=true -J-Djava.rmi.server.useCodebaseOnly=false
        // NB: aggiungi la bin folder di java 9 al path di windows
        // NB: usa la cmd line di windows (no powershell)
        // il game è da due, perciò lancia due client per far inizare una partita in automatico
        IController controller = new Controller(2);
        System.out.println(">>> Controller exported");

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("controller", controller);

        while (true) {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(".");
        }

    }
}
