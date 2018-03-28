package projectIngSoft.ToolCardsImpl;

import projectIngSoft.Colour;
import projectIngSoft.ToolCard;
import projectIngSoft.WindowFrame;

public class TamponeDiamantato extends ToolCard {
    public TamponeDiamantato() {
        super("Tampone diamantato", "Dopo aver scelto un dado, giralo sulla faccia opposta\n" +
                "6 diventa 1, 5 diventa 2, 4 diventa 3 ecc.", Colour.GREEN);
    }

    public void applyEffect(WindowFrame window) {

    }
}
