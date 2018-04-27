package project.ing.soft.cards.toolcards;

import project.ing.soft.Colour;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class TaglierinaManuale extends ToolCard {
    public TaglierinaManuale() {
        super("Taglierina manuale", "Muovi fino a due dadi dello\n" +
                "stesso colore di un solo dado sul Tracciato dei Round\n" +
                "Devi rispettare tutte le restrizioni di piazzamento", Colour.BLUE);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
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
