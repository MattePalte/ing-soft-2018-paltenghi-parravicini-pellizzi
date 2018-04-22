package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;
import projectIngSoft.exceptions.MalformedToolCardException;

public class PennelloPastaSalda extends ToolCard {

    private Die dieToRoll;

    public void setToRoll(Die dieToRoll) {
        this.dieToRoll = dieToRoll;
    }

    public PennelloPastaSalda() {
        super("Pennello per pasta salda", "Dopo aver scelto un dado, tira nuovamente quel dado\n" +
                "Se non puoi piazzarlo, riponilo nella Riserva", Colour.VIOLET);
    }


    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p,m);
        m.removeFromDraft(dieToRoll);
        m.addToDraft(dieToRoll.rollDie());
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(dieToRoll);
        validatePresenceOfDieIn(dieToRoll, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
