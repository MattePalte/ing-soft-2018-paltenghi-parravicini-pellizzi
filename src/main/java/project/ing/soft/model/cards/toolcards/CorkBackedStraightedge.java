package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.*;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

public class CorkBackedStraightedge extends ToolCardSingleState {
    private Die chosenDieFromDraft;
    private Coordinate chosenPosition;

    public CorkBackedStraightedge() {
        super("Cork-backed Straightedge", "After drafting, place the\n" +
                "die in a spot that is not adjacent to\nanother die.\n" +
                "You must obey all other\nplacement restrictions.",
                "toolcard/30%/toolcards-10.png", Colour.YELLOW);
    }


    @Override
    public void checkParameters(Player p, IGameModel m) throws MalformedToolCardException {
        WindowPattern pattern = m.getCurrentPlayer().getPattern();

        validateCoordinate(chosenPosition, pattern.getHeight(), pattern.getWidth());
        if(p.isThereAnAdjacentDie(chosenPosition.getRow(), chosenPosition.getCol()))
            throw new MalformedToolCardException("You must choose a position away from other dice!!");
        validateDie(chosenDieFromDraft);
        validatePresenceOfDieIn(chosenDieFromDraft, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        chosenDieFromDraft = acquirer.getDieFromDraft("Choose a die from the draft pool");
        chosenPosition     = acquirer.getCoordinate("Choose a position away from other dice: ");
    }

    @Override
    public void apply(Player p, IGameModel m) throws Exception{
        p.placeDie(chosenDieFromDraft, chosenPosition.getRow(), chosenPosition.getCol(), false);
        m.removeFromDraft(chosenDieFromDraft);
    }

    @Override
    public ToolCard copy() {
        CorkBackedStraightedge cs = new CorkBackedStraightedge();
        cs.chosenDieFromDraft     = this.chosenDieFromDraft;
        cs.chosenPosition         = this.chosenPosition;
        return cs;
    }
}
