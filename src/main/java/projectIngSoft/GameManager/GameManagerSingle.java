package projectIngSoft.GameManager;

import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.Objectives.Publics.PublicObjective;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.Game;
import projectIngSoft.Player;
import projectIngSoft.RoundTracker;
import projectIngSoft.View.LocalViewCli;

import java.util.List;
import java.util.Map;

public class GameManagerSingle implements IGameManager {
    public GameManagerSingle(Game aSinglePlayerGame ) {
    }

    @Override
    public void countPlayersPoints() throws Exception {

    }

    @Override
    public void deliverNewStatus(IGameManager newStatus) {

    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception {

    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {

    }

    @Override
    public List<PrivateObjective> getPrivateObjective() {
        return null;
    }

    @Override
    public void endTurn() {

    }

    @Override
    public List<Player> getPlayerList() {
        return null;
    }

    @Override
    public List<ToolCard> getToolCards() {
        return null;

    }

    @Override
    public List<Die> getDraftPool() {
        return null;
    }

    @Override
    public Game getGameInfo() {
        return null;
    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public List<PublicObjective> getPublicObjective() {
        return null;
    }

    @Override
    public List<Die> getDiceBag() {
        return null;
    }

    @Override
    public Map<Player, Integer> getFavours() {
        return null;
    }

    @Override
    public RoundTracker getRoundTracker() {
        return null;
    }

    @Override
    public List<Player> getRoundTurns() {
        return null;
    }

    @Override
    public void start() {

    }

    private void setupPhase() throws Exception {

    }

    @Override
    public Player getWinner() throws Exception {
        return new Player("Kris", new LocalViewCli());
    }
}
