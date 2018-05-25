package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.MyTurnStartedEvent;
import project.ing.soft.model.gamemanager.events.ToolcardActionRequestEvent;

public class PennelloPastaSaldaSecondPart extends ToolCard {
    private Die dieToBePlaced;
    private Coordinate cord;

    PennelloPastaSaldaSecondPart(Die dieToBePlaced) {
        super("", "", Colour.WHITE, "");
        this.dieToBePlaced = dieToBePlaced;
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(dieToBePlaced);
        validatePresenceOfDieIn(dieToBePlaced, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        cord = acquirer.getCoordinate(String.format("Select where you want to place the die %s ", dieToBePlaced));
    }

    @Override
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            checkParameters(p, m);

            apply(p, m);

            p.update(new ModelChangedEvent(m));
            p.update(new MyTurnStartedEvent());
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

    @Override
    public void apply(Player p, IGameManager m) throws Exception  {

            m.removeFromDraft(dieToBePlaced);
            p.placeDie(dieToBePlaced, cord.getRow(), cord.getCol(), true);
            p.update(new ToolcardActionRequestEvent(this));

    }
}
