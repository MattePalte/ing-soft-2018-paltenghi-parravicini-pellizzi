package projectIngSoft.View;

import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

public class ViewRMI implements IView {


    @Override
    public void update(IGameManager newModel) {

    }

    @Override
    public IGameManager getUpdate() {
        return null;
    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) {

    }

    @Override
    public void playToolCard(ToolCard aToolCard) {

    }

    @Override
    public void endTurn() {

    }
}
