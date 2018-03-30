package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.WindowFrame;

public class Martelletto extends ToolCard {
    public Martelletto() {
        super("Martelletto", "Tira nuovamente tutti i dadi della Riserva Questa carta può essera usata\n" +
                "solo durante il tuo secondo turno, prima di scegliere il secondo dado", Colour.BLUE);
    }

    public void applyEffect(WindowFrame window) {

    }
}
