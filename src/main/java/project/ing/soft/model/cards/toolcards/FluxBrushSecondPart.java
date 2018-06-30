package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.gamemodel.events.ModelChangedEvent;
import project.ing.soft.model.gamemodel.events.MyTurnStartedEvent;

import java.io.Serializable;

public class FluxBrushSecondPart implements IToolCardState, Serializable {
    private final Die dieToBePlaced;
    private Coordinate cord;

    FluxBrushSecondPart(Die dieToBePlaced) {
        this.dieToBePlaced = dieToBePlaced;
    }

    @Override
    public void checkParameters(ToolCard ctx,Player p, IGameModel m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        ctx.validateDie(dieToBePlaced);
        ctx.validatePresenceOfDieIn(dieToBePlaced, m.getDraftPool());
    }

    @Override
    public void fill(ToolCard ctx,IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        cord = acquirer.getCoordinate(String.format("Select where you want to place the die %s value %s",
                dieToBePlaced.getColour().toString(), String.valueOf(dieToBePlaced.getValue())));
    }

    @Override
    public void play(ToolCard ctx,Player p, IGameModel m) throws ToolCardApplicationException {
        try{
            checkParameters(ctx, p, m);

            apply(p, m);

            ctx.setState(new FluxBrushFirstPart());
            p.update(new ModelChangedEvent(m.copy(p)));
            p.update(new MyTurnStartedEvent());
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

    public void apply(Player p, IGameModel m) throws Exception  {
        p.placeDie(dieToBePlaced, cord.getRow(), cord.getCol(), true);
        m.removeFromDraft(dieToBePlaced);
    }
}
