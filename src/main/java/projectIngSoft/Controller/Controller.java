package projectIngSoft.Controller;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.GameInvalidException;

public class Controller implements IController {

    private IGameManager gameManager;

    public Controller(IGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void requestUpdate() {
        gameManager.requestUpdate();
    }

    @Override
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception {
        gameManager.placeDie(aDie, rowIndex, colIndex);
    }

    @Override
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception {
        if (gameManager.getCurrentPlayer().getName().equals(nickname))
        gameManager.playToolCard(aToolCard);
    }

    @Override
    public void endTurn() throws Exception, GameInvalidException {
        gameManager.endTurn();
    }

    @Override
    public void choosePattern(String nickname, Pair<WindowPatternCard, Boolean> couple) throws Exception, GameInvalidException {
        gameManager.bindPatternAndPlayer(nickname, couple);
    }
}
