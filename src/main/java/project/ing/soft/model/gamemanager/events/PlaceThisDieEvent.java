package project.ing.soft.model.gamemanager.events;

import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlaceThisDieEvent implements Event {
    private Die toBePlaced;
    private boolean isValueChoosable;
    private Player player;

    public PlaceThisDieEvent(Die aDie, Player player, boolean isValueChoosable){
        this.toBePlaced = aDie;
        this.isValueChoosable = isValueChoosable;
        this.player = player;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }

    public boolean getIsValueChoosable(){
        return isValueChoosable;
    }

    public Die getToBePlaced(){
        return toBePlaced;
    }

    public ArrayList<Coordinate> getCompatiblePositions(Die aDie){
        return new ArrayList<>(player.getCompatiblePositions(aDie));
    }
}
