package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Die;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class TaglierinaCircolare extends SingleInterationToolcard {

    private Die dieFromDraft;
    private Die dieFromRoundTracker;

    public void setDieFromDraft(Die dieFromDraft) {
        this.dieFromDraft = dieFromDraft;
    }

    public void setDieFromRoundTracker(Die dieFromRoundTracker) {
        this.dieFromRoundTracker = dieFromRoundTracker;
    }

    public TaglierinaCircolare() {
        super("Taglierina circolare", "Dopo aver scelto un dado,\n" +
                "scambia quel dado con un dado sul Tracciato dei Round", Colour.GREEN,
                "toolcard/30%/toolcards-6.png");
    }

    @Override
    public void applyFirst(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            checkParameters(p,m);
            m.removeFromDraft(dieFromDraft);
            m.swapWithRoundTracker(dieFromDraft,dieFromRoundTracker);
            m.addToDraft(dieFromRoundTracker);
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }
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
    public void fillFirst(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}
