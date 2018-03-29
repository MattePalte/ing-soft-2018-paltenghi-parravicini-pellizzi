package projectIngSoft;

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
                if(d.getColour() == colour)
                    counter++;
        return counter;
   }
}
