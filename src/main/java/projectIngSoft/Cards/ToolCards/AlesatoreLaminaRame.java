package projectIngSoft.Cards.ToolCards;


import javafx.util.Pair;
import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public class AlesatoreLaminaRame extends ToolCard {
    public AlesatoreLaminaRame() {
        super("Alesatore per lamina di rame", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.RED);
    }

    private Pair<Integer, Integer> dieCoordinate;

    public Pair<Integer, Integer> getDieCoordinate() {
        return dieCoordinate;
    }

    public void setDieCoordinate(Pair<Integer, Integer> dieCoordinate) {
        this.dieCoordinate = dieCoordinate;
    }

    public void applyEffect(Player p, IGameManager m) throws Exception {
        Die selectedDie = p.getPlacedDice()[dieCoordinate.getKey()][dieCoordinate.getValue()];
        selectedDie.increment();
        p.placeDie(selectedDie, dieCoordinate.getKey(), dieCoordinate.getValue());

    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
