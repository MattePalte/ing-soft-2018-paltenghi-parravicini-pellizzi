package projectIngSoft.exceptions;

import projectIngSoft.Cards.Constraint;

public class ConstraintViolatedException extends Exception{

    public ConstraintViolatedException(String message){
        super(message);
    }
}
