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

    public List<Player> getPlayerList();
    public List<ToolCard> getToolCards();
    public List<Die> getDraftPool();
    public Game getGameInfo();
    public Player getCurrentPlayer();
    public List<PublicObjective> getPublicObjective();
    public List<Die> getDiceBag();
    public Map<Player, Integer> getFavours();
    public RoundTracker getRoundTracker();
    public List<Player> getRoundTurns();
    public Player getWinner()       throws Exception;
    public void playToolCard(ToolCard aToolCard)      throws Exception;
    public void placeDie(Die aDie, int rowIndex, int colIndex)          throws Exception;
    public List<PrivateObjective> getPrivateObjective();
    public void endTurn();
    public void setupPhase()        throws Exception;
    public void countPlayersPoints()   throws Exception;
    public void deliverNewStatus(IGameManager newStatus);
}
