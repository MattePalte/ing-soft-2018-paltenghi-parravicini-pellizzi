package projectIngSoft.Controller;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.exceptions.GameInvalidException;

public interface IController {
    public void requestUpdate();
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception;
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception;
    public void endTurn() throws Exception, GameInvalidException;
    void choosePattern(String nickname, Pair<WindowPatternCard, Boolean> couple) throws Exception, GameInvalidException;
}
