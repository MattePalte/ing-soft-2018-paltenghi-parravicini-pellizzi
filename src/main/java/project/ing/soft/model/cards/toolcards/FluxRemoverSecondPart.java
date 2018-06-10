package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.MyTurnStartedEvent;

import java.io.Serializable;

public class FluxRemoverSecondPart implements IToolCardState, Serializable {
    private final Die toBePlaced;
    private int newValue;
    private Coordinate cord;

    public FluxRemoverSecondPart(Die toBePlaced){
        this.toBePlaced = toBePlaced;
    }

    @Override
    public void checkParameters(ToolCard ctx, Player p, IGameManager m) throws MalformedToolCardException {
        ctx.validatePresenceOfDieIn(toBePlaced, m.getDraftPool());
        ctx.validateCoordinate(cord, p.getPattern().getHeight(), p.getPattern().getWidth());
    }

    @Override
    public void fill(ToolCard ctx, IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
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
    public void play(ToolCard ctx,Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            checkParameters(ctx, p, m);

            apply(p, m);

            ctx.setState(new FluxRemoverFirstPart());
            p.update(new ModelChangedEvent(m.copy()));
            p.update(new MyTurnStartedEvent());
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

    @Override
    public void apply(Player p, IGameManager m) throws Exception {
        p.placeDie(new Die(newValue, toBePlaced.getColour()), cord.getRow(), cord.getCol(), true);
        m.removeFromDraft(toBePlaced);
    }
}
