package project.ing.soft.cards.toolcards;

import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.Colour;

public class StripCutter extends ToolCard {

    public StripCutter() {
        super("Taglia strisce", "Prendi un dado da qualsiasi altro giocatore. Al suo posto dai loro un dado dello stesso colore o dello stesso valore\n" +
                "Possono piazzare il dado senza curarsi delle restrizioni di colore o valore\n" +
                "Deve essere usato prima del Round 7", Colour.WHITE,
                "toolcard/30%/toolcards-14.png");
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
