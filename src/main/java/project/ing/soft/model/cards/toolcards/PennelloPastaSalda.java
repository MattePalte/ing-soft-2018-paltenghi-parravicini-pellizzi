package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Die;
import project.ing.soft.model.Colour;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.gamemanager.events.PlaceThisDieEvent;

public class PennelloPastaSalda extends MultipleInteractionToolcard {

    private Die dieToRoll;

    public void setToRoll(Die dieToRoll) {
        this.dieToRoll = dieToRoll;
    }

    public PennelloPastaSalda() {
        super("Pennello per pasta salda", "Dopo aver scelto un dado, tira nuovamente quel dado\n" +
                "Se non puoi piazzarlo, riponilo nella Riserva", Colour.VIOLET,
                "toolcard/30%/toolcards-7.png");
    }


    @Override
    public void applyEffect(Player p, IGameManager m) throws ToolCardApplicationException {
        try {
            checkParameters(p, m);
            m.removeFromDraft(dieToRoll);
            Die toPlace = dieToRoll.rollDie();
            m.addToDraft(toPlace);
            m.setUnrolledDie(toPlace);
            p.update(new PlaceThisDieEvent(toPlace, new Player(p), false));
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(dieToRoll);
        validatePresenceOfDieIn(dieToRoll, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}
