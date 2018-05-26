package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class Martelletto extends ToolCard {
    public Martelletto() {
        super("Martelletto", "Tira nuovamente tutti i dadi della Riserva Questa carta può essera usata\n" +
                "solo durante il tuo secondo turno, prima di scegliere il secondo dado",
                "toolcard/30%/toolcards-8.png", Colour.BLUE);
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        if(m.getCurrentTurnList().size() > m.getPlayerList().size())
            throw new MalformedToolCardException("This is not your second turn in this round");
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws InterruptedException, UserInterruptActionException {
        //this ToolCard doesn't need any data
    }

    @Override
    public void apply(Player p, IGameManager m) {
        m.rollDraftPool();
    }
}
