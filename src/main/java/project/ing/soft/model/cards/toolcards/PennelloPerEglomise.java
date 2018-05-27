package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.*;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

import java.util.List;

public class PennelloPerEglomise extends ToolCard {

    private Coordinate startPosition;
    private Coordinate endPosition;

    public PennelloPerEglomise() {
        super("Pennello per Eglomise", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore. \n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento",
                "toolcard/30%/toolcards-3.png", Colour.BLUE);
    }


    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateCoordinate(startPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
        validateCoordinate(endPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        startPosition = acquirer.getCoordinate("Enter which die you want to move");
        endPosition   = acquirer.getCoordinate("Enter an empty cell's position to move it");

    }

    @Override
    public void apply(Player p, IGameManager m) throws Exception{
        p.moveDice(List.of(startPosition), List.of(endPosition), false, true, true);
    }
}
