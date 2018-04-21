package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

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
        m.removeFromDraft(dieToRoll);
        m.addToDraft(dieToRoll.rollDie());
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
