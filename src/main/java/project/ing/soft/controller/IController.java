package project.ing.soft.controller;

import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.Die;
import project.ing.soft.view.IView;
import project.ing.soft.model.cards.toolcards.ToolCard;

import java.rmi.Remote;

public interface IController extends Remote{
    public void requestUpdate() throws Exception;
    public void placeDie(String nickname, Die aDie, int rowIndex, int colIndex) throws Exception;
    public void playToolCard(String nickname, ToolCard aToolCard) throws Exception;
    public void endTurn(String nickname) throws Exception;
    void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception;
    void joinTheGame(String nickname, IView view) throws Exception;
}
