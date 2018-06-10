package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Die;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class LensCutter extends ToolCardSingleState {

    private Die dieFromDraft;
    private Die dieFromRoundTracker;

    public LensCutter() {
        super("Lens Cutter", "After drafting, swap the drafted\n" +
                "die with a die from the\nRound Track",
                "toolcard/30%/toolcards-6.png", Colour.GREEN);
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(dieFromDraft);
        validateDie(dieFromRoundTracker);
        validatePresenceOfDieIn(dieFromDraft, m.getDraftPool());
        validatePresenceOfDieIn(dieFromRoundTracker, m.getRoundTracker().getDiceLeftFromRound());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        dieFromDraft = acquirer.getDieFromDraft("Choose from Draft:");
        dieFromRoundTracker = acquirer.getDieFromRound("Chose from RoundTracker:");
    }

    @Override
    public void apply(Player p, IGameManager m) throws ToolCardApplicationException {

        m.removeFromDraft(dieFromDraft);
        m.swapWithRoundTracker(dieFromDraft,dieFromRoundTracker);
        m.addToDraft(dieFromRoundTracker);

    }

    @Override
    public ToolCard copy() {
        LensCutter lc          = new LensCutter();
        lc.dieFromDraft        = this.dieFromDraft;
        lc.dieFromRoundTracker = this.dieFromRoundTracker;
        return lc;
    }
}
