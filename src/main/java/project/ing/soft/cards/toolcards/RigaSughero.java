package project.ing.soft.cards.toolcards;

import project.ing.soft.Coordinate;
import project.ing.soft.Die;
import project.ing.soft.cards.WindowPattern;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Colour;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class RigaSughero extends ToolCard {
    private Die chosenDieFromDraft;
    private Coordinate chosenPosition;

    public void setChosenDie(Die aDie){
        this.chosenDieFromDraft = aDie;
    }

    public void setPosition(Coordinate position){
        this.chosenPosition = position;
    }

    public RigaSughero() {
        super("Riga di sughero", "Dopo aver scelto un dado,\n" +
                "piazzalo in una casella che non sia adiacente a un altro dado\n" +
                "Devi rispettare tutte le restrizioni di piazzamento", Colour.YELLOW,
                "toolcard/30%/toolcards-10.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p, m);
        p.placeDie(chosenDieFromDraft, chosenPosition.getRow(), chosenPosition.getCol(), false);
        m.removeFromDraft(chosenDieFromDraft);
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
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
