package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public class StripCutter extends ToolCard {

    public StripCutter() {
        super("Taglia strisce", "Prendi un dado da qualsiasi altro giocatore. Al suo posto dai loro un dado dello stesso colore o dello stesso valore\n" +
                "Possono piazzare il dado senza curarsi delle restrizioni di colore o valore\n" +
                "Deve essere usato prima del Round 7", Colour.WHITE);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {

    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
