package projectIngSoft.Cards.Objectives.Privates;

import projectIngSoft.Cards.Objectives.ObjectiveCard;
import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.Player;

public class PrivateObjective extends ObjectiveCard {

    private Colour colour;

    public PrivateObjective(String title, String description, int points, Colour colour){
        super(title, description, points);
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

        return new String(title + "\n" + description + "\n" + "Points: "+ points);
   }
}
