package projectIngSoft.Controller;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.GameManagerMulti;
import projectIngSoft.GameManager.GameManagerSingle;
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
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {
        gameManager.placeDie(aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception {
        gameManager.playToolCard(aToolCard);
    }

    @Override
    public void endTurn() throws Exception {
        gameManager.endTurn();
    }
}
