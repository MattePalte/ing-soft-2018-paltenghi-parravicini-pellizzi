package project.ing.soft.model.gamemodel.events;
import project.ing.soft.model.Pair;
import project.ing.soft.model.Player;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameFinishedEvent  implements Event, Serializable {

    private final List<Pair<Player,Integer>> rank;
    private Map<String, String> pointsDescriptor;

    public GameFinishedEvent(List<Pair<Player, Integer>> theRank, Map<String, String> pointsDescriptor) {
        this.rank = theRank;
        this.pointsDescriptor = pointsDescriptor;
    }

    public List<Pair<Player, Integer>> getRank() {
        return rank;
    }

    public Map<String, String> getPointsDescriptor(){
        return pointsDescriptor;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}

