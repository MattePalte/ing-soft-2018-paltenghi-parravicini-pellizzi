package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.Player;

public class PinzaSgrossatrice extends ToolCard {

    private Die choosenDie;
    private boolean toBeIncreased;

    public void setChoosenDie(Die choosenDie) {
        this.choosenDie = new Die(choosenDie);
    }

    public void setToBeIncreased(boolean toBeIncreased) {
        this.toBeIncreased = toBeIncreased;
    }



    public PinzaSgrossatrice() {
        super("Pinza sgrossatrice", "Dopo aver scelto un dado,\n" +
                "aumenta o dominuisci il valore del dado scelto di 1\n" +
                "Non puoi cambiare un 6 in 1 o un 1 in 6" , Colour.VIOLET );
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        m.removeFromDraft(choosenDie);
        if ((choosenDie.getValue() == 6 && toBeIncreased )||(choosenDie.getValue() == 1 && !toBeIncreased )) throw new Exception("invalid operation: 6-> 1 or 1->6");
        Die newDie = (toBeIncreased ? new Die(choosenDie.getValue() + 1, choosenDie.getColour()) : new Die(choosenDie.getValue() - 1, choosenDie.getColour()));
        m.addToDraft(newDie);
        //TODO: decide where to save that a toolCard has been used for the first time fav cost: 1 -> 2
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}