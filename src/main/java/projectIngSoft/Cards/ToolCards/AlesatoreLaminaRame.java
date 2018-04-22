package projectIngSoft.Cards.ToolCards;


import javafx.util.Pair;
import projectIngSoft.Colour;
import projectIngSoft.Coordinate;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

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
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.RED);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p,m);
        p.moveDie(startPosition, endPosition, true, true);
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
