package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.gamemanager.GameManagerMulti;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.MyTurnStartedEvent;
import project.ing.soft.model.gamemanager.events.PlaceThisDieEvent;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

import java.util.ArrayList;

public class DiluentePastaSalda extends MultipleInteractionToolcard {
    private Die chosenDie;

    public void setChosenDie(Die chosenDie){
        this.chosenDie = chosenDie;
    }

    public DiluentePastaSalda() {
        super("Diluente per pasta salda", "Dopo aver scelto un dado, riponilo nel\n" +
                "Sacchetto, poi pescane uno dal Sacchetto Scegli il valore del nuovo dado e\n" +
                "piazzalo, rispettando tutte le restrizioni di piazzamento", Colour.VIOLET,
                "toolcard/30%/toolcards-12.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
        // TODO: Players can now choose the die value and choose where to place it, but if no compatible positions are found or the player doesn't choose a position, the die will be rolled
        System.out.println("Starting applyEffect");
        m.removeFromDraft(chosenDie);
        // Die is placed into the dicebag rolled to avoid to draft it again with the same value in following rounds
        m.addToDicebag(chosenDie.rollDie());
        Die toBePlaced = m.drawFromDicebag().rollDie();
        m.addToDraft(toBePlaced);
        m.setUnrolledDie(toBePlaced);
        p.update(new PlaceThisDieEvent(toBePlaced, new Player(p), true));

        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }

        System.out.println("Terminating applyEffect");
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        validateDie(chosenDie);
        validatePresenceOfDieIn(chosenDie, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}
