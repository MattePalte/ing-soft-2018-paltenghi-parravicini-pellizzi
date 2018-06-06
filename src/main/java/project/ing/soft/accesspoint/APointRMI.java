package project.ing.soft.accesspoint;

import project.ing.soft.controller.IController;
import project.ing.soft.rmi.PlayerControllerOverRmi;
import project.ing.soft.rmi.ViewProxyOverRmi;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class APointRMI extends UnicastRemoteObject implements IAccessPoint{

    private static final String ACCESS_POINT = "accesspoint";
    private final transient Logger log;
    private final AccessPointReal accessPointReal;

    public APointRMI(AccessPointReal accessPointReal) throws RemoteException {
        super();
        this.accessPointReal = accessPointReal;
        this.log = Logger.getLogger(Objects.toString(this));
        this.log.setLevel(Level.OFF);
    }

    public static void bind(APointRMI ap) throws IOException {
        Registry registry ;
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (ExportException ex) {
            registry = LocateRegistry.getRegistry(1099);
        } catch (RemoteException ex) {
            ap.log.log(Level.SEVERE, "error while getting the registry" , ex);
            throw ex;
        }
        System.setProperty("java.rmi.dgc.leaseValue", "10000");
        registry.rebind(ACCESS_POINT, ap);
        ap.log.log(Level.INFO,"AccessPoint RMI published on the registry");
    }

    public static void unbind(APointRMI ap) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        registry.unbind(ACCESS_POINT);
        ap.log.log(Level.INFO,"AccessPoint RMI removed from the registry");
    }

    public static void stopRegistry() throws RemoteException {
            Registry registry = LocateRegistry.getRegistry();
            UnicastRemoteObject.unexportObject(registry, true);
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
        proxyOverRmi.attachController(newController);
        proxyOverRmi.start();

        return (proxyOverRmi);
    }

    @Override
    public IController reconnect(String nickname, String code, IView clientView) throws Exception {
        log.log(Level.INFO,"{0} requested to reconnect", nickname);
        ViewProxyOverRmi proxyOverRmi = new ViewProxyOverRmi(clientView, nickname);
        IController newController = accessPointReal.reconnect(nickname, code, clientView);
        // POST CONNECT ->
        proxyOverRmi.attachController(newController);
        proxyOverRmi.start();
        return (proxyOverRmi);
    }

}
