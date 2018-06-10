package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.*;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;

import java.util.ArrayList;

public class TapWheel extends ToolCardSingleState {
    private Die dieFromRoundTracker;
    private ArrayList<Coordinate> diceChosen;
    private ArrayList<Coordinate> moveTo;

    public TapWheel() {
        super("Tap Wheel", "Move up to two dice of the same\n"+
                "color that match the color of a die on the Round Track.\n" +
                "You must obey all placement restrictions",
                "toolcard/30%/toolcards-13.png", Colour.BLUE);

    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        WindowPattern pattern = m.getCurrentPlayer().getPattern();
        Die[][] placedDice = m.getCurrentPlayer().getPlacedDice();
        if(diceChosen == null || moveTo == null )
            throw new MalformedToolCardException("The Toolcard wasn't filled");
        validateDie(dieFromRoundTracker);
        validatePresenceOfDieIn(dieFromRoundTracker, m.getRoundTracker().getDiceLeftFromRound());
        if(diceChosen.size() > 2)
            throw new MalformedToolCardException("You can choose at most two dice of the same colour of the one chosen from the roundtracker");
        for(Coordinate pos : diceChosen){
            validateCoordinate(pos, pattern.getHeight(), pattern.getWidth());
            if(placedDice[pos.getRow()][pos.getCol()] == null || !placedDice[pos.getRow()][pos.getCol()].getColour().equals(dieFromRoundTracker.getColour()))
                throw new MalformedToolCardException("You must choose dice with the same colour of the die chosen from the roundtracker");
        }

    }

    @Override
    public void fill(IToolCardParametersAcquirer acquirer) throws UserInterruptActionException, InterruptedException {
        diceChosen = new ArrayList<>();
        moveTo = new ArrayList<>();

        dieFromRoundTracker = acquirer.getDieFromRound("Choose a color from the round tracker: ");

        diceChosen.add(acquirer.getCoordinate("Choose the position of a " + dieFromRoundTracker.getColour() + " placed die in your pattern"));
        moveTo.add(acquirer.getCoordinate("Choose where you want to move the die you have just chosen"));
        diceChosen.add(acquirer.getCoordinate("Choose the position of a " + dieFromRoundTracker.getColour() + " placed die in your pattern"));
        moveTo.add(acquirer.getCoordinate("Choose where you want to move the die you have just chosen"));


    }

    @Override
    public void apply(Player p, IGameManager m) throws Exception {
        p.moveDice(diceChosen, moveTo, true, true, true);
    }

    @Override
    public ToolCard copy() {
       TapWheel tw              = new TapWheel();
       tw.diceChosen            = this.diceChosen;
       tw.dieFromRoundTracker   = this.dieFromRoundTracker;
       tw.moveTo                = this.moveTo;
       return tw;
    }
}
