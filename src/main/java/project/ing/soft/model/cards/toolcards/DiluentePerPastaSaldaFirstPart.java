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

import javax.tools.Tool;

public class DiluentePerPastaSaldaFirstPart extends ToolCard {
    private DiluentePerPastaSalda father;
    private Die chosenDie;
    private Die toBePlaced;

    DiluentePerPastaSaldaFirstPart(DiluentePerPastaSalda father){
        super("", "", Colour.WHITE,"");
        this.father = father;
    }
    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        validateDie(chosenDie);
        validatePresenceOfDieIn(chosenDie, m.getDraftPool());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        chosenDie = acquirer.getDieFromDraft("Choose a die to take back to the dice bag: ");
    }

    @Override
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        try{
            m.canPayToolCard(father);

            checkParameters(p, m);

            apply(p, m);
            m.payToolCard(father);

            ToolCard secondStage = new DiluentePerPastaSaldaSecondPart(toBePlaced);
            father.setState(secondStage);
            p.update(new ModelChangedEvent(m));
            p.update(new ToolcardActionRequestEvent(secondStage));
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }

    }

    @Override
    void apply(Player p, IGameManager m) throws Exception {

        m.removeFromDraft(chosenDie);
        // Die is placed into the dice bag rolled to avoid to draft it again with the same value in following rounds
        m.addToDicebag(chosenDie);
        toBePlaced = new Die(m.drawFromDicebag().getColour());
        m.addToDraft(toBePlaced);


    }
}
