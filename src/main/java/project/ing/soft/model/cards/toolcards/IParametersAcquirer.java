package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;

public interface IParametersAcquirer {
    Die getDieFromDraft(String message);
    Die getDieFromRound(String message);
    Coordinate getCoordinate(String message);
    int getValue(String message, int[] values);
}
