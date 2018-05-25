package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.gamemanager.events.ModelChangedEvent;
import project.ing.soft.model.gamemanager.events.ToolcardActionRequestEvent;

public class PennelloPastaSaldaFirstPart extends ToolCard {

    private PennelloPastaSalda father;
    private Die dieToRoll;
    private Die toPlace;

    PennelloPastaSaldaFirstPart(PennelloPastaSalda father){
       super( "","", Colour.WHITE, "" );
       this.father = father;
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateDie(dieToRoll);
        validatePresenceOfDieIn(dieToRoll, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        dieToRoll = acquirer.getDieFromDraft("Select a die in order to re-roll it");
    }

    @Override
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            m.canPayToolCard(father);
            checkParameters(p, m);

            apply(p, m);

            m.payToolCard(father);

            ToolCard secondStage = new PennelloPastaSaldaSecondPart(toPlace);
            father.setState(secondStage);
            p.update(new ModelChangedEvent(m));
            p.update(new ToolcardActionRequestEvent(secondStage));
        }catch (Exception ex){
            throw new ToolCardApplicationException(ex);
        }
    }

    @Override
    public void apply(Player p, IGameManager m) {

            m.removeFromDraft(dieToRoll);
            toPlace = dieToRoll.rollDie();
            m.addToDraft(toPlace);


    }
}
