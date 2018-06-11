package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.model.Colour;

public class StripCutter extends ToolCardSingleState {

    public StripCutter() {
        super("Strip Cutter", "Take one die from any player. " +
                        "Give them a die of a matching Color or Value. " +
                "They may place it ignoring Color or Value Restrictions. " +
                "May be used before Round 7.",
                "toolcard/30%/toolcards-14.png", Colour.WHITE);
    }



    @Override
    public void checkParameters(Player p, IGameModel m) throws MalformedToolCardException {
        throw new MalformedToolCardException("Used a not supported ToolCard");
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void apply(Player p, IGameModel m) throws ToolCardApplicationException {
        throw new ToolCardApplicationException(new UnsupportedOperationException());
    }

    @Override
    public ToolCard copy() {
        return this;
    }
}
