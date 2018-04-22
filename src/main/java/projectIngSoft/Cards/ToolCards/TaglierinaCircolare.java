package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class TaglierinaCircolare extends ToolCard {

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
                "scambia quel dado con un dado sul Tracciato dei Round", Colour.GREEN);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p,m);
        m.removeFromDraft(dieFromDraft);
        m.swapWithRoundTracker(dieFromDraft,dieFromRoundTracker);
        m.addToDraft(dieFromRoundTracker);
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
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
