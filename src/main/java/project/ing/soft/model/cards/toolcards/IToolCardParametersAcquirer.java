package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;

import java.io.Serializable;

/**
 * These interface enable data exchange between user and ToolCard.
 * Every actor that recall {@link ToolCard#fill(IToolCardParametersAcquirer)}
 * should implements this interface in order to expose, in a general way, the specific mechanism
 * the player is using to interact with the game.
 */
public interface IToolCardParametersAcquirer extends Serializable {
    /**
     * Request player to supply a die that is contained in the DraftPool
     * @param message a message that could explicitly state a particular class of die to be chosen
     * @return the die chosen by the player
     * @throws InterruptedException if the request has been interrupted externally
     * @throws UserInterruptActionException if the user request to abort the procedure
     */
    Die getDieFromDraft(String message) throws InterruptedException, UserInterruptActionException;
    /**
     * Request player to supply a die from the Round Tracker
     * @param message a message that could explicitly state a particular class of die to be chosen
     * @return the die chosen by the player
     * @throws InterruptedException if the request has been interrupted externally
     * @throws UserInterruptActionException if the user request to abort the procedure
     */
    Die getDieFromRound(String message)  throws InterruptedException, UserInterruptActionException;
    /**
     * Request player to supply a coordinate of his game board
     * @param message a message that could explicitly state a particular class of coordinate to be chosen
     * @return the position chosen by the user
     * @throws InterruptedException if the request has been interrupted externally
     * @throws UserInterruptActionException if the user request to abort the procedure
     */
    Coordinate getCoordinate(String message) throws InterruptedException, UserInterruptActionException;
    /**
     * Request player to choose a value from the list supplied
     * @param message that could clarify the request
     * @return the value chosen by the player
     * @throws InterruptedException if the request has been interrupted externally
     * @throws UserInterruptActionException if the user request to abort the procedure
     */
    int getValue(String message, Integer... values)  throws InterruptedException, UserInterruptActionException;
    /**
     * Request a player to answer a boolean question
     * @param message the message to be printed
     * @return true if user choose "yes", false otherwise
     * @throws InterruptedException if timeout expires while user is making his choice
     * @throws UserInterruptActionException if user abort operation
     */
    boolean getAnswer(String message) throws InterruptedException, UserInterruptActionException;
}
