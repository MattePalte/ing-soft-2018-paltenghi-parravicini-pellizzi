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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IGameManager {
    Game                    getGameInfo();

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
    void addPlayer(Player player)                       throws Exception;
    void playToolCard(ToolCard aToolCard)               throws Exception;
    void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception;
    void removeFromDraft(Die aDie);
    void addToDraft(Die aDie);
    void bindPatternAndPlayer(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception, GameInvalidException;
    void endTurn()                                      throws Exception;
    void swapWithRoundTracker(Die toAdd, Die toRemove);
    List<Pair<Player, Integer>> countPlayersPoints()    throws Exception;

    void requestUpdate()                                throws RemoteException;
    void deliverNewStatus(Event event)                  throws RemoteException;


}
