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

public class FluxRemoverFirstPart implements IToolCardState, Serializable {

    private Die chosenDie;
    private Die toBePlaced;


    public void checkParameters(ToolCard ctx,Player p, IGameModel m) throws MalformedToolCardException {
        ctx.validateDie(chosenDie);
        ctx.validatePresenceOfDieIn(chosenDie, m.getDraftPool());
    }

    @Override
    public void fill(ToolCard ctx, IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        chosenDie = acquirer.getDieFromDraft("Choose a die to take back to the dice bag: ");
    }

    @Override
    public void play(ToolCard ctx, Player p, IGameModel m) throws ToolCardApplicationException {
        try{
            m.canPayToolCard(ctx);

            checkParameters(ctx, p, m);

            apply(p, m);
            m.payToolCard(ctx);

            ctx.setState(new FluxRemoverSecondPart(toBePlaced));
            p.update(new ModelChangedEvent(m.copy()));
            p.update(new ToolcardActionRequestEvent(ctx.copy()));
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }

    }

    @Override
    public void apply( Player p, IGameModel m) {

        m.removeFromDraft(chosenDie);
        // Die is placed into the dice bag rolled to avoid to draft it again with the same value in following rounds
        m.addToDicebag(chosenDie);
        toBePlaced = new Die(m.drawFromDicebag().getColour());
        m.addToDraft(toBePlaced);


    }
}
