package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Player;

public class PennelloPerEglomise extends ToolCard {
    public PennelloPerEglomise() {
        super("Pennello per Eglomise", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.BLUE);
    }

    @Override
    public void applyEffect(Player p) {

    }
}
