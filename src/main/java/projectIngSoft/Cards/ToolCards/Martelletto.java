package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class Martelletto extends ToolCard {
    public Martelletto() {
        super("Martelletto", "Tira nuovamente tutti i dadi della Riserva Questa carta può essera usata\n" +
                "solo durante il tuo secondo turno, prima di scegliere il secondo dado", Colour.BLUE);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {

    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {

    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
