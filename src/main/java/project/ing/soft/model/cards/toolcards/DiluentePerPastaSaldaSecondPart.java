package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.MyTurnStartedEvent;

public class DiluentePerPastaSaldaSecondPart extends ToolCard {
    private Die toBePlaced;
    private int newValue;
    private Coordinate cord;

    public DiluentePerPastaSaldaSecondPart(Die toBePlaced){
        super("", "", Colour.WHITE, "");
        this.toBePlaced = toBePlaced;
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        validateDie(toBePlaced);
        validatePresenceOfDieIn(toBePlaced, m.getDraftPool());
        validateCoordinate(cord, p.getPattern().getHeight(), p.getPattern().getWidth());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        newValue = acquirer.getValue(String.format("A die of color %s was chosen. Choose the new value for it", toBePlaced.getColour().name()), 1,2,3,4,5,6);
        cord     = acquirer.getCoordinate("Where do you want to place this new die?");
    }

    /**
     * This particular play does not pay any favour and terminate the usage of diluente per pasta salda
     * @param p
     * @param m
     * @throws ToolCardApplicationException
     */
    @Override
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            checkParameters(p, m);

            apply(p, m);

            p.update(new ModelChangedEvent(m));
            p.update(new MyTurnStartedEvent());
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

    @Override
    public void apply(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            checkParameters(p, m);
            m.removeFromDraft(toBePlaced);
            p.placeDie(new Die(newValue, toBePlaced.getColour()), cord.getRow(), cord.getCol(), true);
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }

    }
}
