package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Colour;

import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

import java.util.List;

public class Lathekin extends ToolCard {

    private Coordinate firstDieStartPosition;
    private Coordinate firstDieEndPosition;
    private Coordinate secondDieStartPosition;
    private Coordinate secondDieEndPosition;

    public void setFirstDieStartPosition(Coordinate firstDieStartPosition) {
        this.firstDieStartPosition = firstDieStartPosition;
    }

    public void setFirstDieEndPosition(Coordinate firstDieEndPosition) {
        this.firstDieEndPosition = firstDieEndPosition;
    }

    public void setSecondDieStartPosition(Coordinate secondDieStartPosition) {
        this.secondDieStartPosition = secondDieStartPosition;
    }

    public void setSecondDieEndPosition(Coordinate secondDieEndPosition) {
        this.secondDieEndPosition = secondDieEndPosition;
    }

    public Lathekin() {
        super("Lathekin", "Muovi esattamente due dadi,\n" +
                "rispettando tutte le restrizioni di piazzamento", Colour.YELLOW,
                "toolcard/30%/toolcards-5.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p,m);

        p.moveDice( List.of(firstDieStartPosition,secondDieStartPosition),
                    List.of(firstDieEndPosition, secondDieEndPosition),
                    true, true, true);

    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateCoordinate(firstDieStartPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
        validateCoordinate(firstDieEndPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
        validateCoordinate(secondDieStartPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
        validateCoordinate(secondDieEndPosition, p.getPattern().getHeight(), p.getPattern().getHeight());
    }

    @Override
    public void fill(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}