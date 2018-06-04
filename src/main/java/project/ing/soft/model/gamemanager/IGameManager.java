package project.ing.soft.model.gamemanager;

import project.ing.soft.exceptions.RuleViolatedException;
import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.Die;
import project.ing.soft.model.Game;
import project.ing.soft.exceptions.GameInvalidException;
import javafx.util.Pair;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.Player;
import project.ing.soft.model.RoundTracker;
import project.ing.soft.view.IView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Main class of any already started game. It contains the business logic of the game
 * and all the data about the model's state (draftpool situation, player's situation,
 * turn list, etc).
 * Each IGameManager represent uniquely a game (or a match as a synonym) intended as that
 * object that contains from 2 to 4 player. At the same time the IGameManager is the unique entity
 * capable of modifying its internal state, and as a consequence the flow of the game.
 */
public interface IGameManager extends Serializable {

    /**
     * Methods to ask the GameManager basic info about the current Game
     * @return Game object containing basic information (e.g. players' list)
     */
    Game getGameInfo();

    /**
     * Methods to get the current status (enumeration)
     * @return one constant of the enumeration GAME_MANAGER_STATUS
     */
    GAME_MANAGER_STATUS     getStatus();

    /**
     * List of players that are registerd to this match
     * @return player's list
     */
    List<Player>            getPlayerList();

    /**
     * Get a reference to the current player
     * @return current player
     */
    Player                  getCurrentPlayer();

    /**
     * Returns a list of players representing the turn list of a
     * specific round. Each player is present twice and the order
     * represents exactly the order of the player's turn for the current
     * round
     * @return turn list
     */
    List<Player>            getCurrentTurnList();

    List<ToolCard>          getToolCards();
    List<PublicObjective>   getPublicObjective();
    List<Card>              getPublicCards();

    List<Die>               getDraftPool();
    Map<String, Integer>    getFavours();
    RoundTracker            getRoundTracker();

    Player getWinner()                                  throws Exception;

    /**
     * Remove a random Die from the Dice Bag. Its value is also random.
     * @return random Die from Dice Bag
     */
    Die drawFromDicebag();

    /**
     * Methods to removed a given Die from the draft pool
     * @param aDie
     */
    void    removeFromDraft(Die aDie);

    /**
     * Methods to add a given Die to the draft pool
     * @param aDie
     */
    void    addToDraft(Die aDie);

    /**
     * Method to roll every die in the current draft pool
     */
    void    rollDraftPool();

    /**
     * Method to add a given Die to the DiceBag
     * @param aDie
     */
    void    addToDicebag(Die aDie);

    /**
     * It swaps the to given dice. The toAdd is added to the round tracker
     * while the toRemove is removed from it.
     * @param toAdd die to add to the RoundTracker
     * @param toRemove die to remove from the RoundTracker
     */
    void    swapWithRoundTracker(Die toAdd, Die toRemove);

    void    setupPhase()    throws Exception;
    void    samePlayerAgain();

    void    bindPatternAndPlayer(String nickname, WindowPatternCard windowCard,
                                 Boolean side) throws Exception, GameInvalidException;


    void    payToolCard(ToolCard toolCard);
    void    canPayToolCard(ToolCard aToolCard) throws RuleViolatedException;
    void    playToolCard(ToolCard aToolCard)               throws Exception;

    void    placeDie(Die aDie, int rowIndex, int colIndex) throws Exception;

    void    endTurn(boolean timeoutOccurred)                throws Exception;


    List<Pair<Player, Integer>> countPlayersPoints()    throws Exception;

    void    requestUpdate()                                throws  Exception;

    IGameManager copy();

    void reconnectPlayer(String playerName, IView view);
    void disconnectPlayer(String playerToDisconnect);

    enum GAME_MANAGER_STATUS{
        WAITING_FOR_PATTERNCARD, ONGOING, ENDED
    }
}
