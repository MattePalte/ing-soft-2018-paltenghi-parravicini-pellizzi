package project.ing.soft.model.cards.toolcards;


import project.ing.soft.exceptions.*;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class CopperFoilBurnisher extends ToolCardSingleState {

    private Coordinate startPosition;
    private Coordinate endPosition;

    public CopperFoilBurnisher() {
        super("Copper Foil Burnisher", "Move any one die in your window ignoring shade restriction. " +
                "You must obey all other placement restrictions",
                "toolcard/30%/toolcards-4.png", Colour.RED);
    }

    @Override
    public void checkParameters(Player p, IGameModel m) throws MalformedToolCardException {
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
    public void apply(Player p, IGameModel m) throws Exception {
        p.moveDice(
                new ArrayList<>(Arrays.asList(startPosition)),
                new ArrayList<>(Arrays.asList(endPosition)),
                true, false, true);
    }

    @Override
    public ToolCard copy() {
        CopperFoilBurnisher al = new CopperFoilBurnisher();
        al.endPosition   = this.endPosition;
        al.startPosition = this.startPosition;
        return al;
    }
}
