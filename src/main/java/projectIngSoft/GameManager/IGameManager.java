package projectIngSoft.GameManager;

import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.Objectives.Publics.PublicObjective;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.Game;
import projectIngSoft.Player;
import projectIngSoft.RoundTracker;

import java.util.List;
import java.util.Map;

public interface IGameManager {
    Game           getGameInfo();
    // | redundant problem of visibility : players do not have to see other's private objective. During serialization these must be accomplished
    // v
    List<Player>   getPlayerList();
    Player         getCurrentPlayer();
    // | remove x2
    // v
    List<ToolCard> getToolCards();
    List<PublicObjective> getPublicObjective();
    List<PrivateObjective> getPrivateObjective();

    List<Card>     getPublicCards();
    List<Die>      getDraftPool();

    // | remove : problem of security
    // v
    List<Die> getDiceBag();

    Map<Player, Integer> getFavours();

    RoundTracker    getRoundTracker();
    List<Player>    getRoundTurns();

    Player getWinner()       throws Exception;


    void start() throws Exception;
    void playToolCard(ToolCard aToolCard)      throws Exception;
    void placeDie(Die aDie, int rowIndex, int colIndex)          throws Exception;
    void endTurn() throws Exception;
    void countPlayersPoints()   throws Exception;
    // | what?
    // v
    void requestUpdate();
    void deliverNewStatus(IGameManager newStatus);

    // Can't call it clone because it clashes with Object.clone()
    IGameManager clone();

}
