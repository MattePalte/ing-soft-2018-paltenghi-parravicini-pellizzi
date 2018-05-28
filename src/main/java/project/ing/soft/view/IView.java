package project.ing.soft.view;

import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.controller.IController;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IView extends Remote{
    void update(Event event) throws IOException;
    void attachController(IController gameController) throws IOException;
    void run() throws IOException;
}
