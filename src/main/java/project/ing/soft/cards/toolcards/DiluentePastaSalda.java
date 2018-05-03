package project.ing.soft.cards.toolcards;

import project.ing.soft.Coordinate;
import project.ing.soft.Die;
import project.ing.soft.events.PlaceThisDieEvent;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.Colour;

import java.util.ArrayList;

public class DiluentePastaSalda extends ToolCard {
    private Die chosenDie;

    public void setChosenDie(Die chosenDie){
        this.chosenDie = chosenDie;
    }

    public DiluentePastaSalda() {
        super("Diluente per pasta salda", "Dopo aver scelto un dado, riponilo nel\n" +
                "Sacchetto, poi pescane uno dal Sacchetto Scegli il valore del nuovo dado e\n" +
                "piazzalo, rispettando tutte le restrizioni di piazzamento", Colour.VIOLET);
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        // TODO: Make current player place the die drawn from the dicebag, if he can
        m.removeFromDraft(chosenDie);
        Die toBePlaced = m.drawFromDicebag();
        ArrayList<Coordinate> compatiblePositions = new ArrayList<>(p.getCompatiblePositions(toBePlaced));
        if(!compatiblePositions.isEmpty()){
            p.update(new PlaceThisDieEvent(toBePlaced, compatiblePositions));
        }
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        validateDie(chosenDie);
        validatePresenceOfDieIn(chosenDie, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardFiller visitor) {
        visitor.fill(this);
    }
}
