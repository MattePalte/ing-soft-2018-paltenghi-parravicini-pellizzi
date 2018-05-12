package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class Martelletto extends ToolCard {
    public Martelletto() {
        super("Martelletto", "Tira nuovamente tutti i dadi della Riserva Questa carta può essera usata\n" +
                "solo durante il tuo secondo turno, prima di scegliere il secondo dado", Colour.BLUE,
                "toolcard/30%/toolcards-8.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p,m);
        m.rollDraftPool();
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        if(m.getCurrentTurnList().size() > m.getPlayerList().size())
            throw new MalformedToolCardException("This is not your second turn in this round");
    }

    // Nothing to be filled here
    @Override
    public void fill(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}
