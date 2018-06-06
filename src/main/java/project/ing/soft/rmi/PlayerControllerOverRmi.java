package project.ing.soft.rmi;

import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.gamemanager.events.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author D.Parravicini
 * This class represent an rmi-user view of services exposed by the server
 * Every instance of this class is engaged in a 1-1 relationship with a client that uses rmi to get to the server.
 * It has the responsibility of ensuring that a user do not fake it's name.
 */
public class PlayerControllerOverRmi implements IController, Remote, Unreferenced {
    private final GameController realController;
    private final String associatedNickname;
    private final Logger logger;

    /**
     * When an instance of this class is created a
     * @param realController is needed which reflects the actual controller of the game the user is playing
     * @param associatedNickname . Since this kind of controller has a 1-1 relationship with the user connected
     *                                      to the game. The name of the actual user has to be said explicitly. This parameter can be
     *                                      used in order to avoid other user to imitate another user in the game
     * @throws RemoteException when the object gets an error while being exported
     */
    PlayerControllerOverRmi(GameController realController, String associatedNickname) throws RemoteException {
        this.realController = realController;
        this.associatedNickname = associatedNickname;
        this.logger = Logger.getLogger(this.getClass().getCanonicalName()+"("+ associatedNickname +")");
        this.logger.setLevel(Level.SEVERE);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void requestUpdate() throws Exception {
        checkController();
        realController.requestUpdate();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        logger.log(Level.INFO, "Player {0} request to place die {1} on ({2},{3})", new Object[]{nickname, aDie, rowIndex, colIndex});
        checkController();
        checkNickname(nickname);
        realController.placeDie(nickname, aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        logger.log(Level.INFO,"{0} request to play the ToolCard: {1} ", new Object[]{nickname, aToolCard});
        checkController();
        checkNickname(nickname);
        realController.playToolCard(nickname, aToolCard);
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        logger.log(Level.INFO,"{0} request to end his turn", nickname);
        checkController();
        checkNickname(nickname);
        realController.endTurn(nickname);
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        logger.log(Level.INFO,"{0} inform the game that he has chosen {1}", new Object[]{nickname, side ? windowCard.getFrontPattern().getTitle(): windowCard.getRearPattern().getTitle()});
        checkController();
        checkNickname(nickname);
        realController.choosePattern(nickname, windowCard, side);
    }

    /**
     * Helper method to test correctness of operations
     * @throws GameInvalidException if the associated controller is null
     */
    private void checkController() throws GameInvalidException {
        if(realController == null) {
            logger.log(Level.SEVERE, "Player {0} request was abandoned because no RealController is connected", associatedNickname);
            throw new GameInvalidException("An ");
        }
    }

    /**
     * Helper method that checks nickname misuse
     * @param nickname name to be tested
     * @throws GameInvalidException whenever the user tried to fake his name
     */
    private void checkNickname(String nickname) throws GameInvalidException{
        if(realController != null && !nickname.equals(associatedNickname)) {
            logger.log(Level.SEVERE, "Player {0} pretend to act like he was {1}.", new Object[]{nickname, nickname});
            realController.markAsDisconnected(associatedNickname);
            throw  new GameInvalidException("You tried to fake your nickname. You'll be kicked off");
        }
    }

    @Override
    public String getControllerSecurityCode() {
        return null;
    }

    /**
     * This method according to <a href="https//docs.oracle.com/javase/8/docs/technotes/guides/rmi/faq.html#leases2"> reference </a> gets called when
     * no registry contains a reference to this object and therefore it can be distributed-garbage-collected. In this way we can exploit user disconnection.
     * Sadly as far as it seems there's no insurance that this method it's called when a VM crashes.
     * In that case we have to rely on exception thrown by {@link project.ing.soft.view.IView#update(Event)} to detect user disconnection.
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/rmi/faq.html#fastleases">here</a>
     */
    @Override
    public void unreferenced() {
       realController.markAsDisconnected(associatedNickname);
       logger.log(Level.INFO, "Player {0} was disconnected due to an Unreferenced call ",associatedNickname);
    }

}
