package projectIngSoft.View;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

public interface IView {
    public void update(IGameManager newModel);
    public void endTurn();
    public void takeTurn();
    public String askForSomething(String aString);
    //TODO : overload su askForSOmething per avere più richieste specifiche
}
