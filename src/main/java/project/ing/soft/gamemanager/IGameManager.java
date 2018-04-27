package project.ing.soft.gamemanager;

import project.ing.soft.cards.objectives.publics.PublicObjective;
import project.ing.soft.Die;
import project.ing.soft.Game;
import project.ing.soft.events.Event;
import project.ing.soft.exceptions.GameInvalidException;
import javafx.util.Pair;
import project.ing.soft.cards.Card;
import project.ing.soft.cards.toolcards.ToolCard;
import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.Player;
import project.ing.soft.RoundTracker;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IGameManager {
    Game getGameInfo();

    List<Player>            getPlayerList();
    Player                  getCurrentPlayer();
    List<ToolCard>          getToolCards();
    List<PublicObjective>   getPublicObjective();
    List<Card>              getPublicCards();
    List<Die>               getDraftPool();
    Map<Player, Integer>    getFavours();
    RoundTracker            getRoundTracker();
    List<Player>            getCurrentTurnList();

    Player getWinner()                                  throws Exception;
    void start()                                        throws Exception;
    void setupPhase()                                   throws RemoteException;
    void playToolCard(ToolCard aToolCard)               throws Exception;
    void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception;
    void removeFromDraft(Die aDie);
    void addToDraft(Die aDie);
    void bindPatternAndPlayer(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception, GameInvalidException;
    void endTurn()                                      throws Exception;
    void swapWithRoundTracker(Die toAdd, Die toRemove);
    void rollDraftPool();
    List<Pair<Player, Integer>> countPlayersPoints()    throws Exception;

    void requestUpdate()                                throws RemoteException;
    void deliverNewStatus(Event event)                  throws RemoteException;

    void addToDicebag(Die aDie);

    void drawFromDicebag();

    void samePlayerAgain();
}
