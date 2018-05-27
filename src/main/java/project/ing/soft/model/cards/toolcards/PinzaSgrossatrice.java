package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.RuleViolatedException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Die;
import project.ing.soft.model.Colour;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class PinzaSgrossatrice extends ToolCard {

    private Die choosenDie;
    private boolean toBeIncreased;

    public PinzaSgrossatrice() {
        super("Pinza sgrossatrice", "Dopo aver scelto un dado, \n" +
                "aumenta o diminuisci il valore del dado scelto di 1. \n" +
                "Non puoi cambiare un 6 in 1 o un 1 in 6" ,
                "toolcard/30%/toolcards-2.png", Colour.VIOLET);
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(choosenDie);
        validatePresenceOfDieIn(choosenDie, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {

        choosenDie =  acquirer.getDieFromDraft("Choose a die on which apply the effect of the ToolCard");
        toBeIncreased = acquirer.getValue("Do you want the value to be increase or decreased?", -1,+1 ) == 1;

    }

    @Override
    public void apply(Player p, IGameManager m) throws Exception {
        m.removeFromDraft(choosenDie);
        if ((choosenDie.getValue() == 6 && toBeIncreased) || (choosenDie.getValue() == 1 && !toBeIncreased))
            throw new RuleViolatedException("invalid operation: 6-> 1 or 1->6");
        Die newDie = toBeIncreased ? choosenDie.increment() : choosenDie.decrement();
        m.addToDraft(newDie);
    }
}