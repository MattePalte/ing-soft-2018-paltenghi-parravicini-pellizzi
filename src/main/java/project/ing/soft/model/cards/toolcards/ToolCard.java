package project.ing.soft.model.cards.toolcards;

import project.ing.soft.Settings;
import project.ing.soft.exceptions.*;
import project.ing.soft.model.*;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.gamemanager.IGameManager;

import java.io.Serializable;
import java.util.List;

/**
 * The ToolCard class defines general behaviour of the play ToolCard action.
 * It's responsible of carrying information alongside the network and define
 * operations that can be performed with them.
 * Compared to other cards (Objectives, patternCards) it's not static.
 */
public abstract class ToolCard implements Serializable, Card {

    private final String title;
    private final String description;
    private final String imgPath;
    private final Colour colour;
    private IToolCardState state;

    /**
     * ToolCard default constructor
     * @param title of the ToolCard
     * @param description human-readable description of the function that would be applied
     * @param imgPath path of the image that graphically represent the card
     * @param colour color of the die that has to be paid in order to play this ToolCard in a solo-fashion game
     */
    public ToolCard(String title, String description, String imgPath, Colour colour, IToolCardState state) {
        this.title       = title;
        this.description = description;
        this.imgPath     = imgPath;
        this.colour      = colour;
        this.state       = state;
    }

    /**
     * Copy constructor for ToolCard
     * @param from a ToolCard to copy from
     */
    public ToolCard(ToolCard from){
        this.title       = from.title;
        this.description = from.description;
        this.imgPath     = from.imgPath;
        this.colour      = from.colour;
        this.state       = from.state;
    }

    public void setState(IToolCardState newState) {
        state = newState;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImgPath() {
        return imgPath;
    }

    @Override
    public String getDescription() {
        return description;
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
    public void fill(IToolCardParametersAcquirer acquirer) throws InterruptedException, UserInterruptActionException {
        state.fill(this, acquirer);
    }
    /**
     * Play ToolCard is a command interface for operations that involves ToolCard
     * Methods that redefine this method has to redefine event raised
     * @param p current player
     * @param m model
     * @throws ToolCardApplicationException describes the reason that cause an error
     */
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        state.play(this, p, m);
    }

    /**
     * Apply ToolCard is a command interface for operations that involves ToolCard
     * Methods that redefine has a chance to define a particular action to be carried
     * @param p current player
     * @param m model
     * @throws ToolCardApplicationException describes the reason that
     */
    void apply(Player p, IGameManager m) throws Exception{
        state.apply(p, m);
    }

    /**
     * CheckParameters is a method that concerns about the soundness of the data written in it
     * @param p current player on which the ToolCard will be applyed
     * @param m model on which the ToolCard will be applied
     * @throws MalformedToolCardException if any error in private data stored in it are wrong
     */
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException{
        state.checkParameters(this, p, m);
    }

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

    @Override
    public String toString() {
        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.SINGLELINEROUNDEDCORNER(),Settings.instance().getTEXT_CARD_WIDTH(), Settings.instance().getTEXT_CARD_HEIGHT());
        aBuilder.appendInAboxToTop(getTitle());
        aBuilder.appendToTop(getDescription());
        return aBuilder.toString();
    }

    public abstract ToolCard copy();

}
