package projectIngSoft.Controller;

import javafx.util.Pair;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.View.IView;
import projectIngSoft.exceptions.GameInvalidException;

import java.rmi.Remote;

public interface IController extends Remote{
    public void requestUpdate() throws Exception;
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception;
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception;
    public void endTurn() throws Exception, GameInvalidException;
    void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception, GameInvalidException;
    void addPlayer(String player, IView view) throws Exception;
}
