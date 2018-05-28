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
import project.ing.soft.model.gamemanager.events.ToolcardActionRequestEvent;

import java.io.Serializable;

public class PennelloPastaSaldaSecondPart implements IToolCardState, Serializable {
    private Die dieToBePlaced;
    private Coordinate cord;

    PennelloPastaSaldaSecondPart(Die dieToBePlaced) {
        this.dieToBePlaced = dieToBePlaced;
    }

    public void checkParameters(ToolCardStateful ctx,Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        ctx.validateDie(dieToBePlaced);
        ctx.validatePresenceOfDieIn(dieToBePlaced, m.getDraftPool());
    }


    public void fill(ToolCardStateful ctx,IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        cord = acquirer.getCoordinate(String.format("Select where you want to place the die %s ", dieToBePlaced));
    }

    public void play(ToolCardStateful ctx,Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            checkParameters(ctx, p, m);

            apply(p, m);

            ctx.setState(new PennelloPastaSaldaFirstPart());
            p.update(new ModelChangedEvent(m.copy()));
            p.update(new MyTurnStartedEvent());
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

    public void apply(Player p, IGameManager m) throws Exception  {
        m.removeFromDraft(dieToBePlaced);
        p.placeDie(dieToBePlaced, cord.getRow(), cord.getCol(), true);
    }
}
