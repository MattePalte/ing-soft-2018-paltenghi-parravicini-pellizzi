package project.ing.soft.cards.toolcards;

import project.ing.soft.Colour;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class TenagliaRotelle extends ToolCard {
    public TenagliaRotelle() {
        super("Tenaglia a rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\n" +
                "Salta il tuo secondo turno in questo round", Colour.RED);
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
