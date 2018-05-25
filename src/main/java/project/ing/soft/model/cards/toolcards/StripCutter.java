package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class StripCutter extends ToolCard {

    public StripCutter() {
        super("Taglia strisce", "Prendi un dado da qualsiasi altro giocatore. Al suo posto dai loro un dado dello stesso colore o dello stesso valore\n" +
                "Possono piazzare il dado senza curarsi delle restrizioni di colore o valore\n" +
                "Deve essere usato prima del Round 7", Colour.WHITE,
                "toolcard/30%/toolcards-14.png");
    }



    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        throw new MalformedToolCardException("Used a not supported ToolCard");
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void apply(Player p, IGameManager m) throws ToolCardApplicationException {
        throw new ToolCardApplicationException(new UnsupportedOperationException());
    }
}
