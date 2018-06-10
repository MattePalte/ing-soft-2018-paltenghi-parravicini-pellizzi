package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.Die;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class GrindingStone extends ToolCardSingleState {
    private Die chosenDie;

    public GrindingStone() {
        super("Grinding Stone", "After drafting, flip the die\nto its opposite side." +
                "E.g. 6 flips to 1, 5 to 2, 4 to 3 ecc.",
                "toolcard/30%/toolcards-11.png", Colour.GREEN);
    }


    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        validateDie(chosenDie);
        validatePresenceOfDieIn(chosenDie, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        chosenDie = acquirer.getDieFromDraft("Choose a die from the draft pool: ");
    }

    @Override
    public void apply(Player p, IGameManager m) {

        m.removeFromDraft(chosenDie);
        m.addToDraft(chosenDie.flipDie());

    }

    @Override
    public ToolCard copy() {
        GrindingStone gs = new GrindingStone();
        gs.chosenDie = this.chosenDie;
        return gs;
    }
}
