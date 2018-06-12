package project.ing.soft.model.cards;

import project.ing.soft.model.Die;
import project.ing.soft.model.Colour;

import java.io.Serializable;
import java.net.URL;

/**
 * Immutable class that represent a x-y cell in WindowPattern
 * Its representation consist of two fields
 * value must be a number between 0 and 6
 * color must be a @link{project.ing.soft.model.Colour}
 */
public class Constraint implements Serializable {
    private final int value;     // 0 if no value constraint is applied
    private final Colour colour; // WHITE if no colour constraint is applied

    /**
     * The Constraint class provides several ways of creating a constraint
     * @param aValue a number between 0 and 6
     * @param aColour an immutable object from {@link project.ing.soft.model.Colour}
     */
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

    public boolean compatibleWith(Die aDie){
        return compatibleWithColour(aDie) && compatibleWithValue(aDie);
    }

    public boolean compatibleWithColour(Die aDie){
        return this.colour == Colour.WHITE || aDie.getColour() == this.colour;
    }

    public boolean compatibleWithValue(Die aDie){
        return this.value == 0 || aDie.getValue() == this.value;
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
            //abbiamo provato 2000+2004, 2001, 3000,2007
            encoding = " ";// \u2000\u2005" con Courier new-> ma finalmente è arrivato il dejavù
        }else{
            encoding = (new Die( value , Colour.WHITE)).toString();
        }

        return encoding;
    }

    public String getImgPath(){
        if(getColour() != Colour.WHITE || getValue() == 0)
            return "";
        String path = String.format("/windowpatterns/dice/constraint/%d.jpg", getValue());
        URL urlResource =  Constraint.class.getResource(path);
        if(urlResource != null) {
            return path;
        }
        return "";
    }

    /**
     * Factory function that returns a constraint from a "XY" string
     * @param rep X must be a number between 0-6 , Y must be a character among "WBRGYV"
     * @return a constraint
     * @throws Colour.ColorNotFoundException if Y character does not fall nto range value
     */
    public static Constraint fromEncoding(String rep) throws Colour.ColorNotFoundException {
       return new Constraint((int) rep.charAt(0) - (int) '0', Colour.valueOf(rep.charAt(1)));
    }
}
