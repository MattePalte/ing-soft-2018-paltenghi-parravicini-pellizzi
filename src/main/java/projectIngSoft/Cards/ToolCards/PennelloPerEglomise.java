package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Coordinate;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class PennelloPerEglomise extends ToolCard {

    private Coordinate startPosition;
    private Coordinate endPosition;

    public void setStartPosition(Coordinate startPosition) {
        this.startPosition = new Coordinate(startPosition);
    }

    public void setEndPosition(Coordinate endPosition) {
        this.endPosition = new Coordinate(endPosition);
    }

    public PennelloPerEglomise() {
        super("Pennello per Eglomise", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.BLUE);
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
