package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;

import java.io.Serializable;

public interface IToolCardParametersAcquirer extends Serializable {
    Die getDieFromDraft(String message) throws InterruptedException, UserInterruptActionException;
    Die getDieFromRound(String message)  throws InterruptedException, UserInterruptActionException;
    Coordinate getCoordinate(String message) throws InterruptedException, UserInterruptActionException;
    int getValue(String message, Integer... values)  throws InterruptedException, UserInterruptActionException;
}