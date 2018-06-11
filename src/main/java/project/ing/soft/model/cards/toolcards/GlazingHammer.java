package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class GlazingHammer extends ToolCardSingleState {
    public GlazingHammer() {
        super("Running Pliers", "Re-roll all dice in the Draft Pool. This may only be used on your second turn before drafting.",
                "toolcard/30%/toolcards-8.png", Colour.BLUE);
    }

    @Override
    public void checkParameters(Player p, IGameModel m) throws MalformedToolCardException {
        if(m.getCurrentTurnList().size() > m.getPlayerList().size())
            throw new MalformedToolCardException("This is not your second turn in this round");
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) {
        //this ToolCard doesn't need any data
    }

    @Override
    public void apply(Player p, IGameModel m) {
        m.rollDraftPool();
    }

    @Override
    public ToolCard copy() {
        return this;
    }
}
