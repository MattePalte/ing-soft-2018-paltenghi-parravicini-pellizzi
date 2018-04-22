package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class TaglierinaManuale extends ToolCard {
    public TaglierinaManuale() {
        super("Taglierina manuale", "Muovi fino a due dadi dello\n" +
                "stesso colore di un solo dado sul Tracciato dei Round\n" +
                "Devi rispettare tutte le restrizioni di piazzamento", Colour.BLUE);
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
