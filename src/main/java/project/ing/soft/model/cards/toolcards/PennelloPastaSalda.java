package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Colour;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class PennelloPastaSalda extends ToolCard {

    private ToolCard state;

    public PennelloPastaSalda() {
        super("Pennello per pasta salda", "Dopo aver scelto un dado, tira nuovamente quel dado\n" +
                "Se non puoi piazzarlo, riponilo nella Riserva", Colour.VIOLET,
                "toolcard/30%/toolcards-7.png");
        state = new PennelloPastaSaldaFirstPart(this);
    }

    void setState(ToolCard newState){
        this.state = newState;
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
       state.checkParameters(p, m);
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        state.fill(acquirer);
    }

    @Override
    public void apply(Player p, IGameManager m) throws Exception {
        state.apply(p, m);
    }

    @Override
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        state.play(p, m);
    }
}
