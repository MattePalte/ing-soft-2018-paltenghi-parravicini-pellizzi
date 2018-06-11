package project.ing.soft.rmi;

import project.ing.soft.controller.GameController;
import project.ing.soft.controller.IController;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.gamemodel.events.Event;

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
public class PlayerControllerOverRmi extends UnicastRemoteObject implements IController, Remote, Unreferenced {
    private final String associatedNickname;
    private final transient GameController realController;
    private final transient Logger logger;

    /**
     * When an instance of this class is created a
     * @param realController is needed which reflects the actual controller of the game the user is playing
     * @param associatedNickname . Since this kind of controller has a 1-1 relationship with the user connected
     *                                      to the game. The name of the actual user has to be said explicitly. This parameter can be
     *                                      used in order to avoid other user to imitate another user in the game
     */
    PlayerControllerOverRmi(GameController realController, String associatedNickname) throws RemoteException {
        super(0);
        this.realController     = realController;
        this.associatedNickname = associatedNickname;
        this.logger = Logger.getLogger(this.getClass().getCanonicalName()+"("+ associatedNickname +")");
        this.logger.setLevel(Level.SEVERE);
    }

    @Override
    public void requestUpdate() throws Exception {
        checkController();
        realController.requestUpdate();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        logger.log(Level.INFO, "Player {0} request to place die {1} on ({2},{3})", new Object[]{associatedNickname, aDie, rowIndex, colIndex});
        checkController();
        realController.placeDie(associatedNickname, aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        logger.log(Level.INFO,"{0} request to play the ToolCard: {1} ", new Object[]{associatedNickname, aToolCard});
        checkController();
        realController.playToolCard(associatedNickname, aToolCard);
    }

    @Override
    public void endTurn(String nickname) throws Exception {
        logger.log(Level.INFO,"{0} request to end his turn", associatedNickname);
        checkController();
        realController.endTurn(associatedNickname);
    }

    @Override
    public void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception {
        logger.log(Level.INFO,"{0} inform the game that he has chosen {1}", new Object[]{associatedNickname, side ? windowCard.getFrontPattern().getTitle(): windowCard.getRearPattern().getTitle()});
        checkController();
        realController.choosePattern(associatedNickname, windowCard, side);
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
