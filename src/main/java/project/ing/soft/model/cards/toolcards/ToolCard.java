package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.*;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.MyTurnEndedEvent;
import project.ing.soft.model.gamemanager.events.MyTurnStartedEvent;
import project.ing.soft.model.gamemanager.events.ToolcardActionRequestEvent;


import java.io.Serializable;
import java.util.List;

public abstract class ToolCard extends Card implements Serializable{
    protected Colour colour;

    public ToolCard(String aTitle,String description, Colour aColour, String resourcePath){
        super(aTitle, description, resourcePath);
        this.colour = aColour;
    }

    public Colour getColour(){
        return this.colour;
    }

    /**
     * Fill method has to be called in order to fill the ToolCard with parameters
     * These will be stored in private fields and used during apply method
     * @param acquirer is the source of data
     * @throws InterruptedException when the thread executing the fill method has been requested a stop
     * @throws UserInterruptActionException when the user interrupts the fill procedure
     */
    public abstract void fill(IToolCardParametersAcquirer acquirer) throws InterruptedException, UserInterruptActionException;

    /**
     * Play ToolCard is a command interface for operations that involves ToolCard
     * Methods that redefine this method has to redefine event raised
     * @param p current player
     * @param m model
     * @throws ToolCardApplicationException describes the reason that
     */
    public void play(Player p, IGameManager m) throws ToolCardApplicationException
    {
        try{
            m.canPayToolCard(this);
            checkParameters(p, m);

            apply(p, m);

            m.payToolCard(this);

            p.update(new ModelChangedEvent(m));
            p.update(new MyTurnStartedEvent());
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

    /**
     * Apply ToolCard is a command interface for operations that involves ToolCard
     * Methods that redefine has a chance to define a particular action to be carried
     * @param p current player
     * @param m model
     * @throws ToolCardApplicationException describes the reason that
     */
    abstract void apply(Player p, IGameManager m) throws Exception;

    /**
     * CheckParameters is a method that concerns about the soundness of the data written in it
     * @param p current player on which the ToolCard will be applyed
     * @param m model on which the ToolCard will be applied
     * @throws MalformedToolCardException if any error in private data stored in it are wrong
     */
    public abstract void checkParameters(Player p, IGameManager m) throws MalformedToolCardException;

    protected void validateDie(Die aDie) throws MalformedToolCardException {
        if (aDie == null)
            throw new MalformedToolCardException(this.getTitle() + ": no die passed to the ToolCard");
        if (!Colour.validColours().contains(aDie.getColour()))
            throw new MalformedToolCardException(this.getTitle() + ": invalid colour of die passed to the ToolCard");
        if (aDie.getValue() < 1 || aDie.getValue() > 6)
            throw new MalformedToolCardException(this.getTitle() + ": invalid value of die passed to the ToolCard");
    }

    protected void validateCoordinate(Coordinate aCord, int nrRow, int nrCol) throws MalformedToolCardException{
        if (aCord == null)
            throw new MalformedToolCardException(this.getTitle() + ": a die coordinate is missing");
        if (aCord.getRow() > nrRow || aCord.getRow() < 0)
            throw new MalformedToolCardException(this.getTitle() + ": coordinate passed exceed pattern area (row)");
        if (aCord.getCol() > nrCol || aCord.getCol() < 0)
            throw new MalformedToolCardException(this.getTitle() + ": coordinate passed exceed pattern area (col)");
    }

    protected void validatePresenceOfDieIn(Die aDie, List<Die> dieList) throws MalformedToolCardException {
        if (!dieList.contains(aDie))
            throw new MalformedToolCardException(this.getTitle() + ": the die passed is not in the list (draft or roundtracker)");
    }

}
