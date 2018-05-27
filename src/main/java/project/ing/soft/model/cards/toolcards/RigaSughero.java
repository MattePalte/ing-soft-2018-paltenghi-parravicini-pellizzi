package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.*;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

public class RigaSughero extends ToolCard {
    private Die chosenDieFromDraft;
    private Coordinate chosenPosition;

    public RigaSughero() {
        super("Riga di sughero", "Dopo aver scelto un dado, \n" +
                "piazzalo in una casella che non sia adiacente a un altro dado. \n" +
                "Devi rispettare tutte le restrizioni di piazzamento",
                "toolcard/30%/toolcards-10.png", Colour.YELLOW);
    }


    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
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
    public void apply(Player p, IGameManager m) throws Exception{

        p.placeDie(chosenDieFromDraft, chosenPosition.getRow(), chosenPosition.getCol(), false);
        m.removeFromDraft(chosenDieFromDraft);

    }
}
