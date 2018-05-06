package project.ing.soft.cards.objectives.privates;

import project.ing.soft.cards.objectives.ObjectiveCard;
import project.ing.soft.Colour;
import project.ing.soft.Die;
import project.ing.soft.Player;

public class PrivateObjective extends ObjectiveCard {

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

   public String toString(){

        return (title + "\n" + description + "\n" + "Points: "+ points);
   }
}
