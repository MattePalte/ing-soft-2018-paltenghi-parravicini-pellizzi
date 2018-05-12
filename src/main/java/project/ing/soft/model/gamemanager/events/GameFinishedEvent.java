package project.ing.soft.model.gamemanager.events;

import javafx.util.Pair;
import project.ing.soft.model.Player;

import java.io.Serializable;
import java.util.List;

public class GameFinishedEvent  implements Event, Serializable {

    private List<Pair<Player, Integer>> rank;

    public GameFinishedEvent(List<Pair<Player, Integer>> theRank) {
        this.rank = theRank;
    }

    public List<Pair<Player, Integer>> getRank() {
        return rank;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}

