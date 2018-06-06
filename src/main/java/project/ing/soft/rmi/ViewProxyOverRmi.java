package project.ing.soft.rmi;

import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;


public class ViewProxyOverRmi extends Thread implements IView, IController, Unreferenced {

    private final IView rmiView;
    private final ArrayList<Event> eventsToForward;
    private GameController gameController;
    private final String nickname;

    @Override
    public void unreferenced() {
        System.out.println("eliminato");
    }

    public ViewProxyOverRmi(IView rmiView, String nickname) throws RemoteException {
        UnicastRemoteObject.exportObject(this,0);
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

                    rmiView.update(eventsToForward.remove(0));

                }
            }
        }catch (InterruptedException | IOException ignored){
            gameController.markAsDisconnected(nickname);
            //the interrupt exception makes possible the thread ending or
            //the update function raise an exception because the update couldn't be completed
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void requestUpdate() throws Exception {
        if(gameController == null)
            return;
        gameController.requestUpdate();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        if(gameController == null)
            return;
        gameController.placeDie(nickname, aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if(gameController == null)
            return;
        gameController.playToolCard(nickname, aToolCard);
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        if(gameController == null)
            return;
        gameController.endTurn(nickname);
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        if(gameController == null)
            return;
        gameController.choosePattern(nickname, windowCard, side);
    }

    @Override
    public String getControllerSecurityCode() {
        return null;
    }


}
