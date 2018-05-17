package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Die;
import project.ing.soft.model.gamemanager.events.PlaceThisDieEvent;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;


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
        // N.B: players can now choose the die value and place it in a compatible position. However, if they choose the die value and there are no compatible positions,
        // the die draft from the diceBag and added to draftPool won't have the value chosen by the player, but it will be rolled
        System.out.println("Starting applyEffect");
        checkParameters(p, m);
        m.removeFromDraft(chosenDie);
        // Die is placed into the dicebag rolled to avoid to draft it again with the same value in following rounds
        m.addToDicebag(chosenDie);
        Die toBePlaced = m.drawFromDicebag().rollDie();
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
