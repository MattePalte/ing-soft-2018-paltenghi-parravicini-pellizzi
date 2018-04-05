package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Player;

public class TamponeDiamantato extends ToolCard {
    public TamponeDiamantato() {
        super("Tampone diamantato", "Dopo aver scelto un dado, giralo sulla faccia opposta\n" +
                "6 diventa 1, 5 diventa 2, 4 diventa 3 ecc.", Colour.GREEN);
    }

    @Override
    public void applyEffect(Player p) {

    }
}
