package projectIngSoft.ToolCardsImpl;

import projectIngSoft.Colour;
import projectIngSoft.ToolCard;
import projectIngSoft.WindowFrame;

public class RigaSughero extends ToolCard {
    public RigaSughero() {
        super("Riga di sughero", "Dopo aver scelto un dado,\n" +
                "piazzalo in una casella che non sia adiacente a un altro dado\n" +
                "Devi rispettare tutte le restrizioni di piazzamento", Colour.YELLOW);
    }

    public void applyEffect(WindowFrame window) {

    }
}
