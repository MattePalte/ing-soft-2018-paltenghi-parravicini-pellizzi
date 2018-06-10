package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.MyTurnStartedEvent;

public abstract class ToolCardSingleState extends ToolCard{
    public ToolCardSingleState(String title, String description, String imgPath, Colour colour) {
        super(title, description, imgPath, colour, null);
    }

    public ToolCardSingleState(ToolCard from) {
        super(from);
    }

    @Override
    public void setState(IToolCardState newState) {
        //no state is set
    }

    @Override
    public abstract void fill(IToolCardParametersAcquirer acquirer) throws InterruptedException, UserInterruptActionException ;

    @Override
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            m.canPayToolCard(this);
            checkParameters(p, m);

            apply(p, m);

            m.payToolCard(this);

            p.update(new ModelChangedEvent(m.copy()));
            p.update(new MyTurnStartedEvent());
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

}
