package projectIngSoft.Controller;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

public interface IController {
    public IGameManager getUpdate();
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception;
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception;
    public void endTurn() throws Exception;
    void choosePattern(String nickname, Pair<WindowPatternCard, Boolean> couple) throws Exception;
}
