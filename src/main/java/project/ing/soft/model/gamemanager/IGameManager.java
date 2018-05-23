package project.ing.soft.model.gamemanager;

import project.ing.soft.model.cards.objectives.publics.PublicObjective;
import project.ing.soft.model.Die;
import project.ing.soft.model.Game;
import project.ing.soft.exceptions.GameInvalidException;
import javafx.util.Pair;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.toolcards.ToolCard;
import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.Player;
import project.ing.soft.model.RoundTracker;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IGameManager extends Serializable {
    Game getGameInfo();
    GAME_MANAGER_STATUS     getStatus();

    List<Player>            getPlayerList();
    Player                  getCurrentPlayer();
    List<Player>            getCurrentTurnList();

    List<ToolCard>          getToolCards();
    List<PublicObjective>   getPublicObjective();
    List<Card>              getPublicCards();

    List<Die>               getDraftPool();
    Map<String, Integer>    getFavours();
    RoundTracker            getRoundTracker();

    Player getWinner()                                  throws Exception;
    List<Pair<Player, Integer>> countPlayersPoints()    throws Exception;
    Die drawFromDicebag();


    void setupPhase()                                   throws RemoteException, Exception;
    void firstPhaseToolCard(ToolCard aToolCard)               throws Exception;
    void secondPhaseToolCard(ToolCard aToolCard)               throws Exception;
    void placeDie(Die aDie, int rowIndex, int colIndex) throws Exception;
    void removeFromDraft(Die aDie);
    void addToDraft(Die aDie);
    void bindPatternAndPlayer(String nickname, WindowPatternCard windowCard, Boolean side) throws Exception, GameInvalidException;
    void endTurn(boolean timeoutOccurred)                throws Exception;
    void swapWithRoundTracker(Die toAdd, Die toRemove);
    void rollDraftPool();

    void setUnrolledDie(Die aDie);

    void requestUpdate()                                throws RemoteException, Exception;
    void addToDicebag(Die aDie);
    void samePlayerAgain();
    void chooseDie(Die aDie);


    enum GAME_MANAGER_STATUS{
        WAITING_FOR_PATTERNCARD, ONGOING, ENDED
    }
}
