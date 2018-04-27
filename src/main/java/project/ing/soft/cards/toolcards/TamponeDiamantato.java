package project.ing.soft.cards.toolcards;

import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Colour;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class TamponeDiamantato extends ToolCard {
    public TamponeDiamantato() {
        super("Tampone diamantato", "Dopo aver scelto un dado, giralo sulla faccia opposta\n" +
                "6 diventa 1, 5 diventa 2, 4 diventa 3 ecc.", Colour.GREEN);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
