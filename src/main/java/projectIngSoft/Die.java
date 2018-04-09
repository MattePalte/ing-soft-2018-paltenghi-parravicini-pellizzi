package projectIngSoft;

import java.util.Random;
import projectIngSoft.Colour;

public class Die implements Cloneable{
    private final int value;
    private final Colour colour;

    public Die(int value, Colour colour) {
        this.value = value;
        this.colour = colour;
    }

    public Die(Colour colour){
        this(0,colour);
    }

    public int getValue() {
        return value;
    }

    public Colour getColour() {
        return colour;
    }

    // The opposite face of a die is given by subtracting the actual face from 7: OppositeFace = 7 - ActualFace
    public Die flipDie(){
        return new Die(7 - this.value, this.colour);
    }

    public Die rollDie(){
        Random randGen = new Random();
        return new Die(randGen.nextInt(6) + 1, this.colour);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Die(this.value, this.colour);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Die die = (Die) o;

        return value == die.value && colour == die.colour;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + (colour != null ? colour.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String encoding;
        if(this.value == 0)
            encoding = new String("\uD83C\uDFB2");
        else
            encoding = new String(Character.toChars(9855 + value));
        return colour.ColourForeground( encoding );
    }


}
