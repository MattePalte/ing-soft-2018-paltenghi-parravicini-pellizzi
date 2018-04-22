package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class TenagliaRotelle extends ToolCard {
    public TenagliaRotelle() {
        super("Tenaglia a rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\n" +
                "Salta il tuo secondo turno in questo round", Colour.RED);
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
