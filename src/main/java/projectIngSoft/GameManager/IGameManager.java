package projectIngSoft.GameManager;

import javafx.util.Pair;
import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.Objectives.Publics.PublicObjective;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Die;
import projectIngSoft.Game;
import projectIngSoft.Player;
import projectIngSoft.RoundTracker;
import projectIngSoft.events.Event;
import projectIngSoft.exceptions.GameInvalidException;

import java.util.List;
import java.util.Map;

public interface IGameManager {
    Game                    getGameInfo();

    List<Player>            getPlayerList();
    Player                  getCurrentPlayer();

    List<ToolCard>          getToolCards();
    void removeFromDraft(Die aDie);
    void addToDraft(Die aDie);
    List<PublicObjective>   getPublicObjective();
    List<Card>              getPublicCards();
    List<Die>               getDraftPool();
    Map<Player, Integer>    getFavours();

    RoundTracker            getRoundTracker();
    void swapWithRoundTracker(Die toAdd, Die toRemove);
    List<Player>            getCurrentTurnList();

    Player getWinner()                                  throws Exception;
    void start()                                        throws Exception;
    void setupPhase()                                   throws Exception;
    void playToolCard(ToolCard aToolCard)               throws Exception;
    void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception;
    void bindPatternAndPlayer(String nickname, Pair<WindowPatternCard, Boolean> chosenPattern) throws Exception, GameInvalidException;
    void endTurn()                                      throws Exception;
    void countPlayersPoints()                           throws Exception;

    void requestUpdate();
    void deliverNewStatus(Event event);
    // Can't call it clone because it clashes with Object.clone()
    IGameManager clone();

}
