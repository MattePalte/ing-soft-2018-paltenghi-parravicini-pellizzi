package project.ing.soft.cards.toolcards;

import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.Colour;

public class DiluentePastaSalda extends ToolCard {
    public DiluentePastaSalda() {
        super("Diluente per pasta salda", "Dopo aver scelto un dado, riponilo nel\n" +
                "Sacchetto, poi pescane uno dal Sacchetto Scegli il valore del nuovo dado e\n" +
                "piazzalo, rispettando tutte le restrizioni di piazzamento", Colour.VIOLET);
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
