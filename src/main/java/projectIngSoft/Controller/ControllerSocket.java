package projectIngSoft.Controller;


import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

public class ControllerSocket implements IController {


    @Override
    public IGameManager getUpdate() {
        return null;
    }

    @Override
    public void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception {

    }

    @Override
    public void playToolCard(ToolCard aToolCard) throws Exception{

    }

    @Override
    public void endTurn() throws Exception{

    }
}
