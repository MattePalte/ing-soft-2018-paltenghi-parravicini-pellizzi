package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Player;

public class PinzaSgrossatrice extends ToolCard {

    public PinzaSgrossatrice() {
        super("Pinza sgrossatrice", "Dopo aver scelto un dado,\n" +
                "aumenta o dominuisci il valore del dado scelto di 1\n" +
                "Non puoi cambiare un 6 in 1 o un 1 in 6" , Colour.VIOLET );
    }

    @Override
    public void applyEffect(Player p) {

    }
}