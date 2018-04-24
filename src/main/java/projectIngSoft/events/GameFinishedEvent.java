package projectIngSoft.events;

import projectIngSoft.Player;

import java.util.HashMap;

public class GameFinishedEvent  implements Event {

    private HashMap<Player, Integer> rank;

    public GameFinishedEvent(HashMap<Player, Integer> theRank) {
        this.rank = theRank;
    }

    public HashMap<Player, Integer> getRank() {
        return rank;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}

