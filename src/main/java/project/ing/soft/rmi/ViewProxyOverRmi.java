package project.ing.soft.rmi;

import project.ing.soft.controller.IController;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


public class ViewProxyOverRmi implements IView, Runnable {

    private final IView rmiView;
    private final ArrayList<Event> eventsToForward;

    public ViewProxyOverRmi(IView rmiView) {
        this.rmiView = rmiView;
        this.eventsToForward = new ArrayList<>();
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
        //not necessary in this implementation of the view.
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (eventsToForward) {
                    while (eventsToForward.isEmpty()) {
                        eventsToForward.wait();
                    }

                    rmiView.update(eventsToForward.remove(0));

                }
            }
        }catch (InterruptedException | IOException ignored){
            //the interrupt exception makes possible the thread ending or
            //the update function raise an exception because the update couldn't be completed
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public PrintStream getPrintStream() throws Exception{
        return rmiView.getPrintStream();
    }
}
