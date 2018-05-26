package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;

import java.io.Serializable;

public abstract class ToolCardStateful extends ToolCard implements Cloneable, Serializable {
    private IToolCardState state;

    /**
     * ToolCardStateful default constructor
     * @param title of the ToolCard
     * @param description human-readable description of the function that would be applied
     * @param imgPath path of the image that graphically represent the card
     * @param colour color of the die that has to be paid in order to play this ToolCard in a solo-fashion game
     */
    public ToolCardStateful(String title, String description, String imgPath, Colour colour) {
        super(title, description, imgPath, colour);
    }

    /**
     * Copy constructor
     * @param copy
     */
    public ToolCardStateful(ToolCardStateful copy) {
        super(copy);
        this.state = copy.state;
    }


    public void setState(IToolCardState newState) {
        state = newState;
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        state.checkParameters(this, p, m);
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        state.fill(this, acquirer);
    }

    @Override
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        state.play(this, p, m);
    }

    @Override
    void apply(Player p, IGameManager m) throws Exception {
        state.apply(p, m);
    }

    public abstract ToolCardStateful copy();

}
