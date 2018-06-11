package project.ing.soft.view;

import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.controller.IController;

import java.io.IOException;
import java.rmi.Remote;

/**
 * Interface available
 */
public interface IView extends Remote{
    /**
     * An entry point to communicate event to the client
     * @param event to be trasmitted to the view
     * @throws IOException if a network error occurred
     */
    void update(Event event) throws IOException;

    /**
     * It
     * In order to permit view to be reused.
     * E.g. a network failure causes the game to be interrrupted we can restablish a connection
     * and the give back the controller to the view avoiding to re-initialize completely the view
     * @param gameController that can be used to carry out operation on the server
     * @throws IOException whether network error is raised during the operation
     */
    void attachController(IController gameController) throws IOException;
    void run() throws IOException;
}
