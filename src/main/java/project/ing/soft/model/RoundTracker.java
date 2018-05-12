package project.ing.soft.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RoundTracker implements Serializable{
    private int currentRound;
    private ArrayList<Die> diceLeftFromRound;

    public RoundTracker(){
        diceLeftFromRound = new ArrayList<>();
        currentRound = 1;
    }

    public RoundTracker(RoundTracker aRoundTracker){
        this.currentRound = aRoundTracker.currentRound;
        this.diceLeftFromRound = new ArrayList<>(aRoundTracker.diceLeftFromRound);

    }

    public int getCurrentRound() {
        return currentRound;
    }

    public ArrayList<Die> getDiceLeftFromRound() {
        return new ArrayList<>(diceLeftFromRound);
    }

    public void swapDie(Die toAdd, Die toRemove){
        //TODO: handle invalid die necessary? or is it prohibited by default to have such a situation?
        diceLeftFromRound.remove(toRemove);
        diceLeftFromRound.add(toAdd);
    }

    public void addDiceLeft(ArrayList<Die> list){
        diceLeftFromRound.addAll(list);
    }

    public void nextRound(){
        currentRound++;
    }

}
