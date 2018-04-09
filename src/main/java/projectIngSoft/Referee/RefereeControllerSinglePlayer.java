package projectIngSoft.Referee;

import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.Game;
import projectIngSoft.Player;

import java.util.List;

public class RefereeControllerSinglePlayer implements RefereeController {
    public RefereeControllerSinglePlayer(Game aSinglePlayerGame ) {
    }

    @Override
    public void countPlayersPoints() throws Exception {

    }

    @Override
    public List<Card> getObjectives() throws Exception {
        return null;
    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception {

    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {

    }

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
    public void setupPhase() throws Exception {

    }

    @Override
    public void watchTheGame() throws Exception {

    }

    @Override
    public Player getWinner() throws Exception {
        return new Player("Kris");
    }
}
