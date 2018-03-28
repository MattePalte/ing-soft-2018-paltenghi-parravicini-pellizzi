package projectIngSoft.ToolCardsImpl;

import projectIngSoft.Colour;
import projectIngSoft.ToolCard;
import projectIngSoft.WindowFrame;

public class AlesatoreLaminaRame extends ToolCard {
    public AlesatoreLaminaRame() {
        super("Alesatore per lamina di rame", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.RED);
    }

    public void applyEffect(WindowFrame window) {

    }
}
