package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.MalformedToolCardException;
import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;


public class DiluentePerPastaSalda extends ToolCard {
    private ToolCard state;


    public DiluentePerPastaSalda() {
        super("Diluente per pasta salda", "Dopo aver scelto un dado, riponilo nel\n" +
                        "Sacchetto, poi pescane uno dal Sacchetto Scegli il valore del nuovo dado e\n" +
                        "piazzalo, rispettando tutte le restrizioni di piazzamento", Colour.VIOLET,
                "toolcard/30%/toolcards-12.png");

        state = new DiluentePerPastaSaldaFirstPart(this);
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
    public void play(Player p, IGameManager m) throws ToolCardApplicationException {
        state.play(p, m);
    }

    @Override
    void apply(Player p, IGameManager m) throws Exception {
        state.apply(p, m);
    }

    DiluentePerPastaSalda copy(){
        DiluentePerPastaSalda copy = new DiluentePerPastaSalda();
        copy.state = copy;
        return copy;
    }

}

