package project.ing.soft.view;

import project.ing.soft.events.Event;
import project.ing.soft.controller.IController;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IView extends Remote{
    void update(Event event) throws RemoteException, Exception;
    void attachController(IController gameController) throws RemoteException, Exception;
    void run() throws Exception;


}
