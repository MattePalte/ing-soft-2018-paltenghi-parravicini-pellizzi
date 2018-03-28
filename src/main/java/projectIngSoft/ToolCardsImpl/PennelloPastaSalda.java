package projectIngSoft.ToolCardsImpl;

import projectIngSoft.Colour;
import projectIngSoft.ToolCard;
import projectIngSoft.WindowFrame;

public class PennelloPastaSalda extends ToolCard {
    public PennelloPastaSalda() {
        super("Pennello per pasta salda", "Dopo aver scelto un dado, tira nuovamente quel dado\n" +
                "Se non puoi piazzarlo, riponilo nella Riserva", Colour.VIOLET);
    }

    public void applyEffect(WindowFrame window) {

    }
}
