package project.ing.soft.model.cards.toolcards;


import project.ing.soft.exceptions.*;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

import java.util.List;

public class AlesatoreLaminaRame extends SingleInterationToolcard {

    private Coordinate startPosition;
    private Coordinate endPosition;

    public void setStartPosition(Coordinate startPosition) {
        this.startPosition = startPosition;
    }

    public void setEndPosition(Coordinate endPosition) {
        this.endPosition = endPosition;
    }

    public AlesatoreLaminaRame() {
        super("Alesatore per lamina di rame", "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore\n" +
                "Devi rispettare tutte le altre restrizioni di piazzamento", Colour.RED,
                "toolcard/30%/toolcards-4.png");
    }

    @Override
    public void applyFirst(Player p, IGameManager m) throws ToolCardApplicationException {
        try {
            checkParameters(p, m);
            //TODO: doesn't have to check value, does it?
            p.moveDice(List.of(startPosition), List.of(endPosition), true, true, true);
        }catch(Exception e){
            throw new ToolCardApplicationException(e);
        }
    }

    @Override
    public void checkParameters(Player p, IGameManager m) throws MalformedToolCardException {
        //check parameters integrity, otherwise send MalformedToolCardException
        validateCoordinate(startPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
        validateCoordinate(endPosition, p.getPattern().getHeight(), p.getPattern().getWidth());
    }

    @Override
    public void fillFirst(IToolCardFiller visitor) throws UserInterruptActionException, InterruptedException {
        visitor.fill(this);
    }
}
