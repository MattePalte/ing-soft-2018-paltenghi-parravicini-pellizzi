package projectIngSoft.ToolCardsImpl;

import projectIngSoft.Colour;
import projectIngSoft.ToolCard;
import projectIngSoft.WindowFrame;

public class Lathekin extends ToolCard {
    public Lathekin() {
        super("Lathekin", "Muovi esattamente due dadi,\n" +
                "rispettando tutte le restrizioni di piazzamento", Colour.YELLOW);
    }

    public void applyEffect(WindowFrame window) {

    }
}
