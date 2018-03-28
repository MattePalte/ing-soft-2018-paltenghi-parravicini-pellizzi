package projectIngSoft;

public class Constraint {
    private final int value; // 0 if no value constraint is applied
    private final Colour colour; // BLANK if no colour constraint is applied

    public Constraint(int aValue, Colour aColour) {
        this.value = aValue;
        this.colour = aColour;
    }
    public Constraint(int aValue) {
        this(aValue,Colour.BLANK);
    }
    public Constraint(Colour aColour) {
        this(0,aColour);
    }
    public Constraint() {
        this(0,Colour.BLANK);
    }

    public boolean checkAll(Dice aDice){
        return checkColour(aDice) && checkValue(aDice);
    }

    public boolean checkColour(Dice aDice){
        return this.colour == Colour.BLANK || aDice.getColour() == this.colour;
    }

    public boolean checkValue(Dice aDice){
        return this.value == 0 || aDice.getColour() == this.colour;
    }

    public int getValue() {
        return value;
    }

    public Colour getColour() {
        return colour;
    }
}
