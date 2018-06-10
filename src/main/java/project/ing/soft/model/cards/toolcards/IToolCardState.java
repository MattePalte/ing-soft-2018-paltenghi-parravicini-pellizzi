package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;

import java.io.Serializable;

/**
 * @author D.Parravicini
 * ToolCardState Interface is intended to provide an interface for stateful ToolCards
 * that want to dinamiccaly change Toolcard actual procedures.
 * Every method has similarities with ToolCard's but recevies an extra parameter in
 * order to make change of actual context.
 * @link {project.ing.soft.model.cards.toolcards.ToolCard}
 */
public interface IToolCardState extends Serializable {

    /**
     * Realization of   @link {project.ing.soft.model.cards.toolcards.ToolCard#fill}
     * in a stateful fashion
     * @param ctx representing the ToolCard actual context
     * @param acquirer helper interface to get parameters from user
     * @throws UserInterruptActionException when user wants to interrupted the action
     * @throws InterruptedException when system request an action interruption
     */
    void fill           (ToolCard ctx, IToolCardParametersAcquirer acquirer)
            throws UserInterruptActionException, InterruptedException ;

    /**
     * Realization of   @link {project.ing.soft.model.cards.toolcards.ToolCard#fill}
     * in a stateful fashion
     * @param ctx representing the ToolCard actual context
     * @param p current player
     * @param m game model
     * @throws MalformedToolCardException when some parameter is clearly wrong
     */
    void checkParameters(ToolCard ctx, Player p, IGameManager m)
            throws MalformedToolCardException;

    /**
     * Realization of   @link {project.ing.soft.model.cards.toolcards.ToolCard#play}
     * in a stateful fashion
     * @param ctx representing the ToolCard actual context
     * @param p current player
     * @param m game model
     * @throws ToolCardApplicationException when the action couldn't be correctly completed
     * because of an error
     */
    void play           (ToolCard ctx, Player p, IGameManager m)
            throws ToolCardApplicationException ;

    /**
     * Realization of   @link {project.ing.soft.model.cards.toolcards.ToolCard#apply}
     * in a stateful fashion
     * @param p current player
     * @param m game model
     * @throws ToolCardApplicationException when the action couldn't be correctly completed
     * because of an error
     */
    void apply          (Player p, IGameManager m)
            throws Exception ;

}
