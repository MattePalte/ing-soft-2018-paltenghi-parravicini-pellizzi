package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.RuleViolatedException;
import project.ing.soft.model.Die;
import project.ing.soft.model.Colour;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class GrozingPliers extends ToolCardSingleState {

    private Die chosenDie;
    private boolean toBeIncreased;

    public GrozingPliers() {
        super("Grozing Pliers", "After drafting,\n increase or decrease the value\nof the drafted die by 1.\n" +
                "1 may not change to 6, or 6 to 1." ,
                "toolcard/30%/toolcards-2.png", Colour.VIOLET);
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(chosenDie);
        validatePresenceOfDieIn(chosenDie, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {

        chosenDie =  acquirer.getDieFromDraft("Choose a die on which apply the effect of the ToolCard");
        toBeIncreased = acquirer.getValue("Do you want the value to be increase or decreased?", -1,+1 ) == 1;

    }

    @Override
    public void apply(Player p, IGameManager m) throws Exception {
        m.removeFromDraft(chosenDie);
        if ((chosenDie.getValue() == 6 && toBeIncreased) || (chosenDie.getValue() == 1 && !toBeIncreased))
            throw new RuleViolatedException("invalid operation: 6-> 1 or 1->6");
        Die newDie = toBeIncreased ? chosenDie.increment() : chosenDie.decrement();
        m.addToDraft(newDie);
    }

    @Override
    public ToolCard copy() {
        GrozingPliers gp = new GrozingPliers();
        gp.chosenDie    = this.chosenDie;
        gp.toBeIncreased= this.toBeIncreased;
        return gp;
    }
}