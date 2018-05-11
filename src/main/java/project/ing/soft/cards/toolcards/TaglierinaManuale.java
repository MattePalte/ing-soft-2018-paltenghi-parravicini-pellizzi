package project.ing.soft.cards.toolcards;

import project.ing.soft.Colour;
import project.ing.soft.Coordinate;
import project.ing.soft.Die;
import project.ing.soft.cards.WindowPattern;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.gamemanager.IGameManager;
import project.ing.soft.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

import java.util.ArrayList;

public class TaglierinaManuale extends ToolCard {
    private Die dieFromRoundTracker;
    private ArrayList<Coordinate> diceChosen;
    private ArrayList<Coordinate> moveTo;

    public void setDieFromRoundTracker(Die dieFromRoundTracker) {
        this.dieFromRoundTracker = dieFromRoundTracker;
    }

    public void setDiceChosen(ArrayList<Coordinate> diceChosen){
        this.diceChosen = diceChosen;
    }

    public void setMoveTo(ArrayList<Coordinate> moveTo){
        this.moveTo = moveTo;
    }

    public TaglierinaManuale() {
        super("Taglierina manuale", "Muovi fino a due dadi dello\n" +
                "stesso colore di un solo dado sul Tracciato dei Round\n" +
                "Devi rispettare tutte le restrizioni di piazzamento", Colour.BLUE,
                "toolcard/30%/toolcards-13.png");
    }

    @Override
    public void applyEffect(Player p, IGameManager m) throws Exception {
        checkParameters(p, m);
        for(int i = 0; i < diceChosen.size(); i++){
            p.moveDie(diceChosen.get(i), moveTo.get(i), true, true, true);
        }
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        WindowPattern pattern = m.getCurrentPlayer().getPattern();
        Die[][] placedDice = m.getCurrentPlayer().getPlacedDice();

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
    public void fill(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}
