package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Coordinate;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class Lathekin extends ToolCard {

    private Coordinate firstDieStartPosition;
    private Coordinate firstDieEndPosition;
    private Coordinate secondDieStartPosition;
    private Coordinate secondDieEndPosition;

    public void setFirstDieStartPosition(Coordinate firstDieStartPosition) {
        this.firstDieStartPosition = firstDieStartPosition;
    }

    public void setFirstDieEndPosition(Coordinate firstDieEndPosition) {
        this.firstDieEndPosition = firstDieEndPosition;
    }

    public void setSecondDieStartPosition(Coordinate secondDieStartPosition) {
        this.secondDieStartPosition = secondDieStartPosition;
    }

    public void setSecondDieEndPosition(Coordinate secondDieEndPosition) {
        this.secondDieEndPosition = secondDieEndPosition;
    }

    public Lathekin() {
        super("Lathekin", "Muovi esattamente due dadi,\n" +
                "rispettando tutte le restrizioni di piazzamento", Colour.YELLOW);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p,m);
        p.moveDie(firstDieStartPosition, firstDieEndPosition, true, true);
        p.moveDie(secondDieStartPosition, secondDieEndPosition, true, true);
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateCoordinate(firstDieStartPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
        validateCoordinate(firstDieEndPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
        validateCoordinate(secondDieStartPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
        validateCoordinate(secondDieEndPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
