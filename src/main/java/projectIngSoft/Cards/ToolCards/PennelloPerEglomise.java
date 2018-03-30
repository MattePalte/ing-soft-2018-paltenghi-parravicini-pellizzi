package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.WindowFrame;

public class PennelloPerEglomise extends ToolCard {
    public PennelloPerEglomise() {
        super("Pennello per Eglomise", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.BLUE);
    }

    public void applyEffect(WindowFrame window) {

    }
}
