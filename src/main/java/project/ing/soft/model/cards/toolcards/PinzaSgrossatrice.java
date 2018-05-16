package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.RuleViolatedException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Die;
import project.ing.soft.model.Colour;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

import javax.tools.Tool;

public class PinzaSgrossatrice extends SingleInterationToolcard {

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
                "Non puoi cambiare un 6 in 1 o un 1 in 6" , Colour.VIOLET,
                "toolcard/30%/toolcards-2.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws ToolCardApplicationException {
        try {
            checkParameters(p, m);
            m.removeFromDraft(choosenDie);
            if ((choosenDie.getValue() == 6 && toBeIncreased) || (choosenDie.getValue() == 1 && !toBeIncreased))
                throw new RuleViolatedException("invalid operation: 6-> 1 or 1->6");
            Die newDie = (toBeIncreased ? new Die(choosenDie.getValue() + 1, choosenDie.getColour()) : new Die(choosenDie.getValue() - 1, choosenDie.getColour()));
            m.addToDraft(newDie);
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }

    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(choosenDie);
        validatePresenceOfDieIn(choosenDie, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}