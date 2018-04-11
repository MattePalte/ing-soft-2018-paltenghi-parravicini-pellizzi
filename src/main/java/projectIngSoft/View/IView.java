package projectIngSoft.View;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

public interface IView {
    public void update(IGameManager newModel);
    public IGameManager getUpdate();
    public void placeDie(Die aDie, int rowIndex, int colIndex);
    public void playToolCard(ToolCard aToolCard);
    public void endTurn();
}
