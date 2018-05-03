package project.ing.soft.events;

import project.ing.soft.Coordinate;
import project.ing.soft.Die;

import java.util.ArrayList;
import java.util.List;

public class PlaceThisDieEvent implements Event {
    private Die toBePlaced;
    private List<Coordinate> compatiblePositions;

    public PlaceThisDieEvent(Die aDie, List<Coordinate> compatiblePositions){
        this.toBePlaced = aDie;
        this.compatiblePositions = compatiblePositions;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }

    public Die getToBePlaced(){
        return toBePlaced;
    }

    public ArrayList<Coordinate> getCompatiblePositions(){
        return new ArrayList<>(compatiblePositions);
    }
}
