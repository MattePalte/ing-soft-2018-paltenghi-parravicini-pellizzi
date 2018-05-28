package project.ing.soft.rmi;

import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class ViewProxyOverRmi extends Thread implements IView {

    private final IView rmiView;
    private final ArrayList<Event> eventsToForward;
    private GameController gameController;
    private final String nickname;

    public ViewProxyOverRmi(IView rmiView, String nickname) {
        this.rmiView = rmiView;
        this.eventsToForward = new ArrayList<>();
        this.nickname = nickname;
    }

    @Override
    public void update(Event event) {
        synchronized (eventsToForward){
            eventsToForward.add(event);
            eventsToForward.notifyAll();
        }
    }

    @Override
    public void attachController(IController gameController) {
        this.gameController = (GameController) gameController;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (eventsToForward) {
                    while (eventsToForward.isEmpty()) {
                        eventsToForward.wait();
                    }
                    try {
                        rmiView.update(eventsToForward.remove(0));
                    } catch (RemoteException ex){
                        gameController.markAsDisconnected(nickname);
                    }
                }
            }
        }catch (InterruptedException | IOException ignored){
            //the interrupt exception makes possible the thread ending or
            //the update function raise an exception because the update couldn't be completed
            Thread.currentThread().interrupt();
        }
    }
}
