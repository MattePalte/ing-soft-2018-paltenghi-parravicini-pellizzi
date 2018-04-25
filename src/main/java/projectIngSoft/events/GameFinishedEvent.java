package projectIngSoft.events;

import javafx.util.Pair;
import projectIngSoft.Player;

import java.util.HashMap;
import java.util.List;

public class GameFinishedEvent  implements Event {

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

