package project.ing.soft;

import java.io.Serializable;
import java.util.Random;

public class Die implements Serializable {
    private static final int FACES = 6;
    private final int value;
    private final Colour colour;

    public Die(int value, Colour colour) {
        this.value = value;
        this.colour = colour;
    }

    public Die(Colour colour){
        this(0,colour);
    }

    public Die(Die aDie){
        this.value = aDie.value;
        this.colour = aDie.colour;
    }

    public int getValue() {
        return value;
    }

    public Colour getColour() {
        return colour;
    }

    // The opposite face of a die is given by subtracting the actual face from 7: OppositeFace = 7 - ActualFace
    public Die flipDie(){
        return new Die(FACES + 1 - this.value, this.colour);
    }

    public Die increment(){
        return this.value < FACES ? new Die(this.value + 1, this.colour) : this;
    }

    public Die rollDie(){
        Random randGen = new Random();
        return new Die(randGen.nextInt(FACES) + 1, this.colour);
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

    public String getXLMescapeEncoding(){
        String encoding;
        if(this.value == 0)
            encoding = " ";
        else
            encoding = new String(Character.toChars(9855 + value));
        return encoding;
    }

    @Override
    public String toString() {
        String encoding;
        if(this.value == 0)
            encoding = "\uD83C\uDFB2";
        else
            encoding = new String(Character.toChars(9855 + value));

        return colour.colourForeground( encoding );
    }


}
