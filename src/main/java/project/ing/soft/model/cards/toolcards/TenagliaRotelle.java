package project.ing.soft.model.cards.toolcards;


import project.ing.soft.model.Colour;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class TenagliaRotelle extends ToolCard {

    public TenagliaRotelle() {
        super("Tenaglia a rotelle", "Dopo il tuo primo turno scegli immediatamente un altro dado." +
                "Salta il tuo secondo turno in questo round",
                "toolcard/30%/toolcards-9.png", Colour.RED);
    }


    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        // Can apply this effect only if it's players's first turn in the round
        if(m.getCurrentTurnList().stream().filter(player -> player.getName().equals(m.getCurrentPlayer().getName())).count() < 2)
            throw new MalformedToolCardException("You can't play this ToolCard: you can only use this during your first turn in the round");
    }


    @Override
    public void fill(IToolCardParametersAcquirer acquirer) {
        // No parameters need to be collected
    }

    @Override
    public void apply(Player p, IGameManager m) {
        m.samePlayerAgain();
    }
}
