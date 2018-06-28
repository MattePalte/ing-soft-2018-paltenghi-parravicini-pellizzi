package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.model.cards.objectives.ObjectiveCard;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.io.Serializable;

/**
 * PrivateObjective class contains all basic information and methods for all private objective
 * cards. This is an extension of class ObjectiveCard.
 */
public class PrivateObjective extends ObjectiveCard implements Serializable {

    private final Colour colour;

    /**
     *  PrivateObjective constructor
     * @param title of the objective card
     * @param description readable information about how to count points
     * @param resourcePath path of the card image
     * @param points points given to a player every time this objective is completed
     * @param colour colour bound to this private objective
     */
    public PrivateObjective(String title, String description, String resourcePath, int points,  Colour colour){
        super(title, description, resourcePath, points);
        this.colour = colour;
    }

    /**
     * This method verifies if the objective is completed by the given player
     * and returns how many times he managed to complete it
     * @param p the player who is counting points on its window
     * @return how many times the condition to complete this objective is achieved
     */
    public int checkCondition(Player p){
        int counter = 0;
        Die[][] placedDice = p.getPlacedDice();

        for(Die[] row : placedDice)
            for(Die d : row)
                if(d!= null && d.getColour() == this.colour)
                    counter += d.getValue();
        return counter;
   }

}
