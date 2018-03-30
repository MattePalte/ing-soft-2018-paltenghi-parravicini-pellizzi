package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.WindowFrame;

public class TaglierinaManuale extends ToolCard {
    public TaglierinaManuale() {
        super("Taglierina manuale", "Muovi fino a due dadi dello\n" +
                "stesso colore di un solo dado sul Tracciato dei Round\n" +
                "Devi rispettare tutte le restrizioni di piazzamento", Colour.BLUE);
    }

    public void applyEffect(WindowFrame window) {

    }
}
