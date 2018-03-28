package projectIngSoft.ToolCardsImpl;

import projectIngSoft.Colour;
import projectIngSoft.ToolCard;
import projectIngSoft.WindowFrame;

public class TenagliaRotelle extends ToolCard {
    public TenagliaRotelle() {
        super("Tenaglia a rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\n" +
                "Salta il tuo secondo turno in questo round", Colour.RED);
    }

    public void applyEffect(WindowFrame window) {


    }
}
