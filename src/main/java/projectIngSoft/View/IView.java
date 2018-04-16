package projectIngSoft.View;

import javafx.util.Pair;
import projectIngSoft.Cards.Card;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;

import java.util.List;

public interface IView {
    void update(IGameManager newModel);
    void attachController(IController gameController);
    void endTurn() throws Exception;
    void takeTurn() throws Exception;

    Pair<WindowPatternCard, Boolean> choose(WindowPatternCard card1, WindowPatternCard card2);
    Die choose(Die[] diceList);
    ToolCard choose(ToolCard[] toolCardList);


    //public String askForSomething(String aString);
    //TODO : overload su askForSOmething per avere più richieste specifiche
}