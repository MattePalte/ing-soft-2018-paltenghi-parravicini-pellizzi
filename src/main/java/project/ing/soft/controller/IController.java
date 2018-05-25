package project.ing.soft.controller;

import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.Die;
import project.ing.soft.view.IView;
import project.ing.soft.model.cards.toolcards.ToolCard;

import java.rmi.Remote;

public interface IController extends Remote{
    void requestUpdate()                                                    throws Exception;
    void placeDie(String nickname, Die aDie, int rowIndex, int colIndex)    throws Exception;
    void PlayToolCard(String nickname, ToolCard aToolCard)                  throws Exception;
    void endTurn(String nickname)                                           throws Exception;
    void choosePattern(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception;
    String getControllerSecurityCode()                                      throws Exception;
    void chooseDie(Die aDie)                                                throws Exception;
}
