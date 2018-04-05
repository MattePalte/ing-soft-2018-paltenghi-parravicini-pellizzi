package projectIngSoft.Referee;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.Game;
import projectIngSoft.Player;

import java.util.List;

public class RefereeControllerMultiplayer implements RefereeController {
    @Override
    public List<ToolCard> getToolCardAvailable() {
        return null;
    }

    @Override
    public List<Die> getDraftPool() {
        return null;
    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public void startGame() {
    }

    public RefereeControllerMultiplayer(Game theGame) {

    }

}
