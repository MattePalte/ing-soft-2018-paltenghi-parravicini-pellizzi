package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.*;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Colour;

import project.ing.soft.model.gamemodel.IGameModel;
import project.ing.soft.model.Player;

import java.util.List;

public class Lathekin extends ToolCardSingleState {

    private Coordinate firstDieStartPosition;
    private Coordinate firstDieEndPosition;
    private Coordinate secondDieStartPosition;
    private Coordinate secondDieEndPosition;

    public Lathekin() {
        super("Lathekin", "Move exactly two dice, \n" +
                "obeying all placement restrictions",
                "toolcard/30%/toolcards-5.png", Colour.YELLOW);
    }

    @Override
    public void checkParameters(Player p, IGameModel m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateCoordinate(firstDieStartPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
        validateCoordinate(firstDieEndPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
        validateCoordinate(secondDieStartPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
        validateCoordinate(secondDieEndPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
    }
    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        firstDieStartPosition  = acquirer.getCoordinate("Enter which is the first die you want to move");
        firstDieEndPosition    = acquirer.getCoordinate("Enter the position in which do you want to move the die");
        secondDieStartPosition = acquirer.getCoordinate("Enter which is the second die you want to move");
        secondDieEndPosition   = acquirer.getCoordinate("Enter the position in which do you want to move the die");
    }

    @Override
    public void apply(Player p, IGameModel m) throws Exception {
            p.moveDice( List.of(firstDieStartPosition,secondDieStartPosition),
                    List.of(firstDieEndPosition, secondDieEndPosition),
                    true, true, true);

    }

    @Override
    public ToolCard copy() {
        Lathekin lt = new Lathekin();
        lt.firstDieStartPosition    = this.firstDieStartPosition;
        lt.firstDieEndPosition      = this.firstDieEndPosition;
        lt.secondDieStartPosition   = this.secondDieStartPosition;
        lt.secondDieEndPosition     = this.secondDieEndPosition;

        return lt;
    }
}
