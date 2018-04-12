package projectIngSoft.Referee;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.Player;

import java.util.List;

public interface RefereeController {
    List<ToolCard> getToolCardAvailable();
    List<Die> getDraftPool();
    Player getCurrentPlayer();
    void startGame() throws Exception;
}
