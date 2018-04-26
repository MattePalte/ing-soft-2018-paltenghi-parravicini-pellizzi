package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.events.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IView extends Remote{
    void update(Event event) throws RemoteException;
    void attachController(IController gameController) throws RemoteException;
    void run() throws Exception;


}
