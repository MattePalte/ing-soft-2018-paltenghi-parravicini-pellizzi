package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.ToolcardActionRequestEvent;

import java.io.Serializable;

public class PennelloPastaSaldaFirstPart implements IToolCardState, Serializable {

    private Die dieToRoll;
    private Die toPlace;


    public void checkParameters(ToolCardStateful ctx,Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        ctx.validateDie(dieToRoll);
        ctx.validatePresenceOfDieIn(dieToRoll, m.getDraftPool());
    }


    public void fill(ToolCardStateful ctx,  IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        dieToRoll = acquirer.getDieFromDraft("Select a die in order to re-roll it");
    }


    public void play(ToolCardStateful ctx, Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            m.canPayToolCard(ctx);
            checkParameters(ctx, p, m);

            apply(p, m);

            m.payToolCard(ctx);


            ctx.setState(new PennelloPastaSaldaSecondPart(toPlace));
            p.update(new ModelChangedEvent(m.copy()));
            p.update(new ToolcardActionRequestEvent(ctx.copy()));
        }catch (Exception ex){
            p.update(new ToolcardActionRequestEvent(ctx.copy()));
            throw new ToolCardApplicationException(ex);
        }
    }

    @Override
    public void apply(Player p, IGameManager m) {
            m.removeFromDraft(dieToRoll);
            toPlace = dieToRoll.rollDie();
            m.addToDraft(toPlace);
    }
}
