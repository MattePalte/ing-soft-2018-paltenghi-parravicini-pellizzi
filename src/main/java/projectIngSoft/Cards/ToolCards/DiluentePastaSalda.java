package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public class DiluentePastaSalda extends ToolCard {
    public DiluentePastaSalda() {
        super("Diluente per pasta salda", "Dopo aver scelto un dado, riponilo nel\n" +
                "Sacchetto, poi pescane uno dal Sacchetto Scegli il valore del nuovo dado e\n" +
                "piazzalo, rispettando tutte le restrizioni di piazzamento", Colour.VIOLET);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {

    }



    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
