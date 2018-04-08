package projectIngSoft.Referee;

import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.Player;

import java.util.List;

public interface RefereeController {
    List<ToolCard> getToolCardAvailable();
    List<Die>      getDraftPool();
    Player         getCurrentPlayer();

    public void setupPhase()        throws Exception;
    public void watchTheGame()      throws Exception;
    public void attributePoints()   throws Exception;
    public Player getWinner()       throws Exception;
    public List<Card> getObjectives()     throws Exception;
}
