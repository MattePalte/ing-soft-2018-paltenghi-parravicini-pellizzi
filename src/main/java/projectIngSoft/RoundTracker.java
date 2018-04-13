package projectIngSoft;

import java.util.ArrayList;

public class RoundTracker {
    private int currentRound;
    private ArrayList<Die> diceLeftFromRound;

    public RoundTracker(){
        diceLeftFromRound = new ArrayList<Die>();
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
        diceLeftFromRound.remove(toRemove);
        diceLeftFromRound.add(toAdd);
    }

    public void addDiceLeft(ArrayList<Die> list){
        diceLeftFromRound.addAll(list);
    }

}
