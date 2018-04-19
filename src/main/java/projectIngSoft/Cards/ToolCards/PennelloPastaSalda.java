package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public class PennelloPastaSalda extends ToolCard {
    public PennelloPastaSalda() {
        super("Pennello per pasta salda", "Dopo aver scelto un dado, tira nuovamente quel dado\n" +
                "Se non puoi piazzarlo, riponilo nella Riserva", Colour.VIOLET);
    }


    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {

    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
