package project.ing.soft.cards.toolcards;


import project.ing.soft.Coordinate;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Colour;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class AlesatoreLaminaRame extends ToolCard {

    private Coordinate startPosition;
    private Coordinate endPosition;

    public void setStartPosition(Coordinate startPosition) {
        this.startPosition = startPosition;
    }

    public void setEndPosition(Coordinate endPosition) {
        this.endPosition = endPosition;
    }

    public AlesatoreLaminaRame() {
        super("Alesatore per lamina di rame", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.RED,
                "toolcard/30%/toolcards-4.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p,m);
        p.moveDie(startPosition, endPosition, true, true, true);
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateCoordinate(startPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
        validateCoordinate(endPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
