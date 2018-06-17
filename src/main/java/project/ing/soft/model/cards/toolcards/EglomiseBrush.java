package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.*;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class EglomiseBrush extends ToolCardSingleState {

    private Coordinate startPosition;
    private Coordinate endPosition;

    public EglomiseBrush() {
        super("Eglomise Brush", "Move any die in your window ignoring the color restrictions. " +
                "You must obey all other placement restrictions.",
                "toolcard/30%/toolcards-3.png", Colour.BLUE);
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
    public void apply(Player p, IGameModel m) throws Exception{
        p.moveDice(
                new ArrayList<Coordinate>(Arrays.asList(startPosition)),
                new ArrayList<Coordinate>(Arrays.asList(endPosition)),
                false, true, true);
    }

    @Override
    public ToolCard copy() {
        EglomiseBrush eb = new EglomiseBrush();
        eb.startPosition = this.startPosition;
        eb.endPosition   = this.endPosition;
        return eb;
    }
}
