package projectIngSoft.Controller;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

public interface IController {
    public IGameManager getUpdate();
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception;
    public void playToolCard(ToolCard aToolCard) throws Exception;
    public void endTurn() throws Exception;

}
