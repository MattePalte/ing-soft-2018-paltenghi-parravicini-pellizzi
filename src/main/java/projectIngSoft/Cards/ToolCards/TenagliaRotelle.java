package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Player;

public class TenagliaRotelle extends ToolCard {
    public TenagliaRotelle() {
        super("Tenaglia a rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\n" +
                "Salta il tuo secondo turno in questo round", Colour.RED);
    }

    @Override
    public void applyEffect(Player p) {

    }
}