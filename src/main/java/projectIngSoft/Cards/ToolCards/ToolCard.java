package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Cards.Card;
import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public abstract class ToolCard extends Card {
    protected Colour colour;
    //TODO: we have to implement ToolCards!

    public ToolCard(String aTitle,String description, Colour aColour){
        super(aTitle, description);
        this.colour = aColour;
    }

    public Colour getColour(){
        return this.colour;
    }
    public abstract void applyEffect(Player p, IGameManager m) throws Exception;

    public abstract void fill(IToolCardFiller visitor);
}
