package projectIngSoft.Cards;

import projectIngSoft.Colour;
import projectIngSoft.Die;

public class Constraint {
    private final int value; // 0 if no value constraint is applied
    private final Colour colour; // WHITE if no colour constraint is applied

    public Constraint(int aValue, Colour aColour) {
        this.value = aValue;
        this.colour = aColour;
    }
    public Constraint(int aValue) {
        this(aValue,Colour.WHITE);
    }
    public Constraint(Colour aColour) {
        this(0,aColour);
    }
    public Constraint() {
        this(0,Colour.WHITE);
    }

    public boolean checkAll(Die aDie){
        return checkColour(aDie) && checkValue(aDie);
    }

    public boolean checkColour(Die aDie){
        return this.colour == Colour.WHITE || aDie.getColour() == this.colour;
    }

    public boolean checkValue(Die aDie){
        return this.value == 0 || aDie.getColour() == this.colour;
    }

    public int getValue() {
        return value;
    }

    public Colour getColour() {
        return colour;
    }

    @Override
    public String toString() {
        String encoding;
        if(value == 0){
            //encoding = "\u3000";
            //encoding = "\u2007";
            //encoding = "\u2001";
            //good "\u2000\u2004";
            encoding = "\u2000\u2005";
        }else{
            encoding = (new Die( value , Colour.WHITE)).toString();
        }

        return encoding;
    }
}
