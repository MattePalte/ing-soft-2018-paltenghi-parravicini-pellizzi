package project.ing.soft.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Random;

/**
 * Representation of a die in the game
 */
public class Die implements Serializable {
    private static final int FACES = 6;
    private final int value;
    private final Colour colour;

    /**
     * Die default constructor
     * @param value of the die upside face
     * @param colour of the die
     */
    public Die(int value, Colour colour) {
        this.value = value;
        this.colour = colour;
    }

    /**
     * Die constructor which creates a die of the given colour with a special value indicating that the die
     * has not been rolled yet
     * @param colour of the die
     */
    public Die(Colour colour){
        this(0,colour);
    }

    /**
     * Die producer. It creates a copy of the die passed as a parameter
     * @param aDie the die to be copied
     */
    public Die(Die aDie){
        this.value = aDie.value;
        this.colour = aDie.colour;
    }

    /**
     *
     * @return the value of the upside face of the die
     */
    public int getValue() {
        return value;
    }

    /**
     *
     * @return the colour of the die
     */
    public Colour getColour() {
        return colour;
    }


    /**
     * This methods flips the die: the upside face goes down and the downside face comes up. The value
     * of the opposite face of a die is computed by subtracting the actual face's value from constant value 7
     * @return a die of the same colour, but with the opposite face upside
     */
    public Die flipDie(){
        return new Die(FACES + 1 - this.value, this.colour);
    }

    /**
     * This method increments the value of the upside face of the die by 1
     * @return a die of the same colour whose value is the value of the given die plus 1
     */
    public Die increment(){
        return this.value < FACES ? new Die(this.value + 1, this.colour) : this;
    }

    /**
     * This method decrements the value of the upside face of the die by 1
     * @return a die of the same colour whose value is the value of the given die minus 1
     */
    public Die decrement() {
        return this.value > 0     ? new Die(this.value - 1, this.colour) : this;
    }

    /**
     * This method rolls the die
     * @return a die of the same colour with a random value on the upside face
     */
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

    /**
     *
     * @return the path of the die's image from the resources folder
     */
    public String getImgPath() {
        if (getColour() == Colour.WHITE || getValue() == 0)
            return "";
        String path = String.format("/windowpatterns/dice/%s/%d.png", getColour().name().toLowerCase(), getValue());
        URL urlResource = Die.class.getResource(path);
        if (urlResource != null) {
            return path;
        }
        return "";
    }

    /**
     *
     * @return a String representation of the die. The String contains UTF-8 characters which represents the
     * the die itself
     */
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
