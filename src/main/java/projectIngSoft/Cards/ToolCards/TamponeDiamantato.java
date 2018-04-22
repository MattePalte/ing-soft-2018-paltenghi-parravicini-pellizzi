package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class TamponeDiamantato extends ToolCard {
    public TamponeDiamantato() {
        super("Tampone diamantato", "Dopo aver scelto un dado, giralo sulla faccia opposta\n" +
                "6 diventa 1, 5 diventa 2, 4 diventa 3 ecc.", Colour.GREEN);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) {

    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {

    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
