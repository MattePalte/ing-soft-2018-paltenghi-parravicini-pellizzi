package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Cards.Card;
import projectIngSoft.Colour;
import projectIngSoft.WindowFrame;

public abstract class ToolCard extends Card {
    protected Colour colour;

    public ToolCard(String aTitle,String description, Colour aColour){
        super(aTitle, description);
        this.colour = aColour;
    }

    public Colour getColour(){
        return this.colour;
    }
    public abstract void applyEffect(WindowFrame window);
}
