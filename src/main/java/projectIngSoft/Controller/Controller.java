package projectIngSoft.Controller;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

public class Controller implements IController {

    IGameManager gameManager;

    public Controller(IGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public IGameManager getUpdate() {
        return gameManager.clone();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        gameManager.placeDie(aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        gameManager.playToolCard(aToolCard);
    }

    @Override
    public void endTurn() throws Exception {
        gameManager.endTurn();
    }

    @Override
    public void choosePattern(String nickname, Pair<WindowPatternCard, Boolean> couple) throws Exception{
        gameManager.bindPatternAndPlayer(nickname, couple);
    }
}
