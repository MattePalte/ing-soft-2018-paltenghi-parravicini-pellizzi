package project.ing.soft.accesspoint;

import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAccessPoint extends Remote{
    IController connect(String nickname, IView clientView) throws Exception;
    IController reconnect(String nickname, String code, IView clientView) throws RemoteException;
}
