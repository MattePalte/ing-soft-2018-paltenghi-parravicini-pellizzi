package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Coordinate;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

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
        p.moveDie(firstDieStartPosition, firstDieEndPosition, true, true);
        p.moveDie(secondDieStartPosition, secondDieEndPosition, true, true);
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
