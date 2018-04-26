package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Cards.Card;
import projectIngSoft.Colour;
import projectIngSoft.Coordinate;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

import java.io.Serializable;
import java.util.List;

public abstract class ToolCard extends Card implements Serializable{
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
    public abstract void checkParameters(Player p, IGameManager m) throws MalformedToolCardException;

    protected void validateDie(Die aDie) throws MalformedToolCardException {
        if (aDie == null)
            throw new MalformedToolCardException(this.getTitle() + ": no die passed to the ToolCard");
        if (!Colour.validColours().contains(aDie.getColour()))
            throw new MalformedToolCardException(this.getTitle() + ": invalid colour of die passed to the ToolCard");
        if (aDie.getValue() < 1 || aDie.getValue() > 6)
            throw new MalformedToolCardException(this.getTitle() + ": invalid value of die passed to the ToolCard");
        return;
    }

    protected void validateCoordinate(Coordinate aCoord, int nrRow, int nrCol) throws MalformedToolCardException{
        if (aCoord == null)
            throw new MalformedToolCardException(this.getTitle() + ": a die coordinate is missing");
        if (aCoord.getRow() > nrRow || aCoord.getRow() < 0)
            throw new MalformedToolCardException(this.getTitle() + ": coordinate passed exceed pattern area (row)");
        if (aCoord.getCol() > nrCol || aCoord.getCol() < 0)
            throw new MalformedToolCardException(this.getTitle() + ": coordinate passed exceed pattern area (col)");
        return;
    }

    protected void validatePresenceOfDieIn(Die aDie, List<Die> dieList) throws MalformedToolCardException {
        if (!dieList.contains(aDie))
            throw new MalformedToolCardException(this.getTitle() + ": the die passed is not in the list (draft or roundtracker)");
        return;
    }



}
