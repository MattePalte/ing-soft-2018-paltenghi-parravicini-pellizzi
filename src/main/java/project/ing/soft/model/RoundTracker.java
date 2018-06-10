package project.ing.soft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an object which indicates the rounds already played and contains the dice left
 * in the Draftpool at the end of them
 */
public class RoundTracker implements Serializable{
    private int currentRound;
    private ArrayList<Die> diceLeftFromRound;

    /**
     * RoundTracker default constructor
     */
    public RoundTracker(){
        diceLeftFromRound = new ArrayList<>();
        currentRound = 1;
    }

    /**
     * RoundTracker producer. It creates a copy of the RoundTracker passed as a parameter
     * @param aRoundTracker the RoundTracker to be copied
     */
    public RoundTracker(RoundTracker aRoundTracker){
        this.currentRound = aRoundTracker.currentRound;
        this.diceLeftFromRound = new ArrayList<>(aRoundTracker.diceLeftFromRound);

    }


    /**
     *
     * @return the indication of the current round
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     *
     * @return a copy of the list of the dice left from the rounds already completed
     */
    public List<Die> getDiceLeftFromRound() {
        return new ArrayList<>(diceLeftFromRound);
    }

    /**
     * This method is used to swap a die in the RoundTracker
     * @param toAdd the die to be added to the RoundTracker
     * @param toRemove the die to be removed from the RoundTracker
     */
    public void swapDie(Die toAdd, Die toRemove){
        //TODO: handle invalid die necessary? or is it prohibited by default to have such a situation?
        diceLeftFromRound.remove(toRemove);
        diceLeftFromRound.add(toAdd);
    }

    /**
     * Method which adds the dice in the list passed as a parameter in the list of dice left from completed
     * rounds. Used to add dice left at the end of a round in the RoundTracker
     * @param list the list of dice to be added to the RoundTracker
     */
    public void addDiceLeft(List<Die> list){
        diceLeftFromRound.addAll(list);
    }

    /**
     * Method which increments the round indicator. Used at the end of a round to signal that a new
     * round is beginning
     */
    public void nextRound(){
        currentRound++;
    }

}
