package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Die;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class TaglierinaCircolare extends ToolCard {

    private Die dieFromDraft;
    private Die dieFromRoundTracker;

    public TaglierinaCircolare() {
        super("Taglierina circolare", "Dopo aver scelto un dado,\n" +
                "scambia quel dado con un dado sul Tracciato dei Round", Colour.GREEN,
                "toolcard/30%/toolcards-6.png");
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
}
