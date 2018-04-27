package project.ing.soft.view;

import project.ing.soft.events.Event;
import project.ing.soft.controller.IController;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IView extends Remote{
    void update(Event event) throws RemoteException;
    void attachController(IController gameController) throws RemoteException;
    void run() throws Exception;


}
