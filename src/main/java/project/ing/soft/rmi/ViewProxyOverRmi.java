package project.ing.soft.rmi;

import project.ing.soft.Settings;
import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Every player that is currently connected to the server has a server instance of a view
 * class that resemble the remote view from which the player can receive notification of events .
 * This is the actual implementation of a view in rmi context.
 */
public class ViewProxyOverRmi extends Thread implements IView {

    private final IView rmiView;
    private final String nickname;
    private final ArrayList<Event> eventsToForward;
    private final Logger log;
    private boolean isStarted;

    private GameController          gameController;
    private PlayerControllerOverRmi controllerOverRmi;

    /**
     * To create a ViewProxyOverRmi an object that implements
     * both {@link IView},{@link java.rmi.Remote} is needed.
     * This would be used in order to send a notification to the player via {@link #update(Event)} method.
     * @param rmiView that extend {@link java.rmi.Remote}
     * @param nickname a name that identifies the player
     */
    public ViewProxyOverRmi(IView rmiView, String nickname) {
        this.rmiView            = rmiView;
        this.eventsToForward    = new ArrayList<>();
        this.nickname           = nickname;
        this.controllerOverRmi  = null;
        this.log                = Logger.getLogger(this.getClass().getCanonicalName()+"("+nickname+")");
        this.log.setLevel(Settings.instance().getDefaultLoggingLevel());
    }

    @Override
    public void update(Event event) {
        log.log(Level.INFO, "Event {0} received", event);
        synchronized (eventsToForward){
            eventsToForward.add(event);
            eventsToForward.notifyAll();
            log.log(Level.INFO, "Event {0} presence notified", event);
        }
    }

    @Override
    public void attachController(IController gameController) {
        this.gameController = (GameController) gameController;
    }

    /**
     * In order to provide an homogeneous user experience regardless of technicalities related to connection method
     * an asynchronous mechanism to exchange data with remotely-connected player was designed using a thread and {@link #update(Event)} method.
     * If an update() get called before the {@link Thread#start()} occurred no event must be missed.
     * The object has to fill a buffer that will be flushed as soon as the thread started.
     * Any error during operation that involves remote method invocation has to result in a call to {@link GameController#markAsDisconnected(String)} method
     */
    @Override
    public void run() {
        isStarted = true;
        log.log(Level.INFO, "Event dispatcher thread started");
        Event aEvent;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (eventsToForward) {
                    while (eventsToForward.isEmpty()) {
                        eventsToForward.wait();
                    }

                    aEvent = eventsToForward.remove(0);
                }
                rmiView.update(aEvent);
            }
        }catch (InterruptedException ex){
            log.log(Level.INFO, "an interrupt was raised");
        } catch(IOException ex){
            log.log(Level.SEVERE, "Event dispatcher thread got an error.", ex);
        }finally {
            log.log(Level.INFO, "Event dispatcher marked {0} as disconnected ", nickname);
            if(isStarted) {
                gameController.markAsDisconnected(nickname);
                //the interrupt exception makes possible the thread ending or
                //the update function raise an exception because the update couldn't be completed
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void interrupt() {
        isStarted = false;
        if(controllerOverRmi != null) {

            try {
                UnicastRemoteObject.unexportObject(controllerOverRmi, true);
            } catch (NoSuchObjectException ex) {
                log.log(Level.SEVERE, "Error while removing controllerOverRmi from registry ", ex);
            }
            controllerOverRmi = null;
            gameController    = null;
        }
        super.interrupt();
    }


    /**
     * On the server the association between player and the channel that can be used to exchange
     * information with the client it's represented by {@link project.ing.soft.model.Player#myView} .
     * While the {@link IView} represent the channel that can carry information from server to client
     * the {@link IController} enable the information flow back from client to server.
     * In order to keep separate this two interfaces we included an internal object
     * @return an exported controller that can be used by an rmi-sagrada-client
     * @throws RemoteException if an error occurred while creating the controller.
     */
    IController buildAStubController() throws RemoteException {
        if (gameController == null)
            return null;
        log.log(Level.INFO, "Stub controller built");
        controllerOverRmi = new PlayerControllerOverRmi(gameController,nickname);

        return controllerOverRmi;
    }
}
