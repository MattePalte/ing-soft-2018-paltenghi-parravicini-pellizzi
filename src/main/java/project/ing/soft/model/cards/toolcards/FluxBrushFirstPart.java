package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.gamemodel.events.ModelChangedEvent;
import project.ing.soft.model.gamemodel.events.ToolcardActionRequestEvent;

import java.io.Serializable;

public class FluxBrushFirstPart implements IToolCardState, Serializable {

    private Die dieToRoll;
    private Die toPlace;


    public void checkParameters(ToolCard ctx,Player p, IGameModel m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        ctx.validateDie(dieToRoll);
        ctx.validatePresenceOfDieIn(dieToRoll, m.getDraftPool());
    }

    @Override
    public void fill(ToolCard ctx,  IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        dieToRoll = acquirer.getDieFromDraft("Select a die in order to re-roll it");
    }

    @Override
    public void play(ToolCard ctx, Player p, IGameModel m) throws ToolCardApplicationException {
        try{
            m.canPayToolCard(ctx);
            checkParameters(ctx, p, m);

            apply(p, m);

            m.payToolCard(ctx);


            ctx.setState(new FluxBrushSecondPart(toPlace));
            p.update(new ModelChangedEvent(m.copy(p)));
            p.update(new ToolcardActionRequestEvent(ctx.copy()));
        }catch (Exception ex){
            p.update(new ToolcardActionRequestEvent(ctx.copy()));
            throw new ToolCardApplicationException(ex);
        }
    }

    @Override
    public void apply(Player p, IGameModel m) {
            m.removeFromDraft(dieToRoll);
            toPlace = dieToRoll.rollDie();
            m.addToDraft(toPlace);
    }
}
