package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Colour;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class TenagliaRotelle extends ToolCard {

    public TenagliaRotelle() {
        super("Tenaglia a rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado\n" +
                "Salta il tuo secondo turno in questo round", Colour.RED,
                "toolcard/30%/toolcards-9.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws ToolCardApplicationException {
        try {
            checkParameters(p, m);
            m.samePlayerAgain();
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        // Can apply this effect only if it's players's first turn in the round
        if(m.getCurrentTurnList().stream().filter(player -> player.getName().equals(m.getCurrentPlayer().getName())).count() < 2)
            throw new MalformedToolCardException("You can't play this toolcard: you can only use this during your first turn in the round");
    }

    // Nothing to fill
    @Override
    public void fill(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}
