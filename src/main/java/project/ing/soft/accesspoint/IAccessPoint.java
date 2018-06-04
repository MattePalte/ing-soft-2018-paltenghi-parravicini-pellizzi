package project.ing.soft.accesspoint;

import project.ing.soft.controller.IController;
import project.ing.soft.view.IView;

import java.rmi.Remote;

/**
 * Represents the interface of the first object the client communicate with.
 * The Access Point function is to provide all the necessary methods for a client
 * to connect to the server and start a game.
 * It is used only during the very first interaction client-server, then the communication
 * pass through a different object called IController which is returned by every methods of
 * the AccessPoint.
 * The AccessPoint let cthe client both connect and reconnect to a game.
 */

public interface IAccessPoint extends Remote{
    /**
     * This method is used by the client to connect to a game and, at the same time,
     * to obtain an IController object. This IController will be used by the client
     * to carry out all the necessary operations throughout the entire game.
     * @param nickname the nickname chosen by the client for that game
     * @param clientView the view object representing the client
     * @return an IController that will be the principal means of communication for the client throughout the entire game
     * @throws Exception
     */
    IController connect(String nickname, IView clientView) throws Exception;

    /**
     * This method is used by the client to re-connect to a game that he/she already joined previously
     * but for some reason (drop of connection or volunteering disconnection) he lost the reference to.
     * In addition to the parameters of a normal connect, this method asks also for a code that helps
     * the server to find the exact game to which the client wants to connect.
     * @param nickname the nickname used by the client in the game when he lost the connection
     * @param code a code to help the server to connect the client to the right game
     * @param clientView the view object representing the client
     * @return an IController that will be the principal means of communication for the client throughout the entire game
     * @throws Exception
     */
    IController reconnect(String nickname, String code, IView clientView) throws Exception;
}
