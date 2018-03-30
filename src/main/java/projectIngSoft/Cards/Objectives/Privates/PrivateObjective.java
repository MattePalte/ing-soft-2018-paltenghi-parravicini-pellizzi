package projectIngSoft.Cards.Objectives.Privates;

import projectIngSoft.Cards.Objectives.ObjectiveCard;
import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.WindowFrame;

public class PrivateObjective extends ObjectiveCard {

    private Colour colour;

    public PrivateObjective(String title, String description, int points, Colour colour){
        super(title, description, points);
        this.colour = colour;
    }

    public int checkCondition(WindowFrame window){
        int counter = 0;
        Die[][] placedDice = window.getPlacedDice();

        for(Die[] row : placedDice)
            for(Die d : row)
                if(d!= null && d.getColour() == this.colour)
                    counter++;
        return counter;
   }
}
