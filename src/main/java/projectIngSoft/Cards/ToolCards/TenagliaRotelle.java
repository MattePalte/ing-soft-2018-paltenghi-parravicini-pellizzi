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
        checkParameters(p, m);
        m.samePlayerAgain();
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        // Can apply this effect only if it's players's first turn in the round
        if(m.getCurrentTurnList().stream().filter(player -> player.getName().equals(m.getCurrentPlayer().getName())).count() < 2)
            throw new MalformedToolCardException("You can't play this toolcard: you can only use this during your first turn in the round");
    }

    // Nothing to fill
    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
