package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public class RigaSughero extends ToolCard {
    public RigaSughero() {
        super("Riga di sughero", "Dopo aver scelto un dado,\n" +
                "piazzalo in una casella che non sia adiacente a un altro dado\n" +
                "Devi rispettare tutte le restrizioni di piazzamento", Colour.YELLOW);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {

    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
