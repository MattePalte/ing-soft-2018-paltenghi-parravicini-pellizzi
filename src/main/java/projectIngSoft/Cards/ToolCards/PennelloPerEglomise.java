package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public class PennelloPerEglomise extends ToolCard {
    public PennelloPerEglomise() {
        super("Pennello per Eglomise", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.BLUE);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {

    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
