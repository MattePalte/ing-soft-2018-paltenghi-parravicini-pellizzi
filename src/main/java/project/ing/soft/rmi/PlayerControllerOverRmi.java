package project.ing.soft.rmi;

import project.ing.soft.controller.IController;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.toolcards.ToolCard;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

public class PlayerControllerOverRmi implements IController, Remote, Unreferenced {
    private final IController realController;

    public PlayerControllerOverRmi(IController realController) throws RemoteException {
        this.realController = realController;
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void requestUpdate() throws Exception {
        if(realController == null)
            return;
        realController.requestUpdate();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        if(realController == null)
            return;
        realController.placeDie(nickname, aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if(realController == null)
            return;
        realController.playToolCard(nickname, aToolCard);
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        if(realController == null)
            return;
        realController.endTurn(nickname);
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        if(realController == null)
            return;
        realController.choosePattern(nickname, windowCard, side);
    }

    @Override
    public String getControllerSecurityCode() {
        return null;
    }


    @Override
    public void unreferenced() {
        System.out.println("unreference!");
    }
}
