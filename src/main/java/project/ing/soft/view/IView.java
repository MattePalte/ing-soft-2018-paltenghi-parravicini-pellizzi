package project.ing.soft.view;

import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.controller.IController;

import java.io.IOException;
import java.rmi.Remote;

public interface IView extends Remote{
    void update(Event event) throws IOException;
    void attachController(IController gameController) throws IOException;
    void run() throws IOException;
}
