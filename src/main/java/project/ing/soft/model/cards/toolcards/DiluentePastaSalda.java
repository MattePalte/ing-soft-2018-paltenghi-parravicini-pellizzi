package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.gamemanager.events.PlaceThisDieEvent;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

import java.util.ArrayList;

public class DiluentePastaSalda extends ToolCard {
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
    public void applyEffect(Player p, IGameManager m) throws Exception {
        // TODO: Make current player place the die drawn from the dicebag, if he can
        m.removeFromDraft(chosenDie);
        m.addToDicebag(chosenDie);
        Die toBePlaced = m.drawFromDicebag().rollDie();
        m.addToDraft(toBePlaced);
        ArrayList<Coordinate> compatiblePositions = new ArrayList<>(p.getCompatiblePositions(toBePlaced));
        if(!compatiblePositions.isEmpty()){
            p.update(new PlaceThisDieEvent(toBePlaced, compatiblePositions, true));
        }
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
