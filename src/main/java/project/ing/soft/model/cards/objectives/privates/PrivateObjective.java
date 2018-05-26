package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.model.cards.objectives.ObjectiveCard;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.io.Serializable;

public class PrivateObjective extends ObjectiveCard implements Serializable {

    private Colour colour;

    public PrivateObjective(String title, String description, String resourcePath, int points,  Colour colour){
        super(title, description, resourcePath, points);
        this.colour = colour;
    }

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
