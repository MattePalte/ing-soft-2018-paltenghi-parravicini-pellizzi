package projectIngSoft.exceptions;

import projectIngSoft.Cards.Constraint;

public class PatternConstraintViolatedException extends Exception{

    public PatternConstraintViolatedException(String message){
        super(message);
    }
}
