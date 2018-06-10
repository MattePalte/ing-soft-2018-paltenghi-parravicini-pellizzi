package project.ing.soft.controller;

import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.toolcards.ToolCard;

import java.rmi.Remote;

/**
 * Interface of a controller. Controller's function is to expose methods to clients to let them call certain
 * operations
 */
public interface IController extends Remote{
    /**
     * Method used to let players request for a model update to the server
     * @throws Exception if anything went wrong while accepting the request
     */
    void requestUpdate()                                                    throws Exception;

    /**
     * Method called to place a die in the placedDice matrix
     * @param nickname name of the player who asked to place a die
     * @param aDie the die asked to be placed
     * @param rowIndex of the position in which the die is asked to be placed
     * @param colIndex of the position in which the die is asked to be placed
     * @throws Exception if anything went wrong while trying to place the die in the asked position or
     * if timeout expires
     */
    void placeDie(String nickname, Die aDie, int rowIndex, int colIndex)    throws Exception;

    /**
     * Method called to play a toolcard.
     * @param nickname of the player who asked to play a toolcard
     * @param aToolCard Toolcard that the player asked to play
     * @throws Exception if anything went wrong while trying to use the asked toolcard or if
     * timeout expires
     */
    void playToolCard(String nickname, ToolCard aToolCard)                  throws Exception;

    /**
     * Method called to signal the end of a turn. This method is also automatically
     * called when the timeout expires
     * @param nickname of the player who is taking its turn
     * @throws Exception if the player who asked to perform operation is not the one who is taking
     * the current turn or if its timeout is already expired
     */
    void endTurn(String nickname)                                           throws Exception;

    /**
     * Method called to make players choose their window pattern.
     * @param nickname name of the player who chose the pattern
     * @param windowCard pattern card chosen by the player
     * @param side a boolean flag which indicated if the player chose the front or the rear side of the card
     * @throws Exception if anything went wrong while binding player and window pattern
     */
    void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception;
}
