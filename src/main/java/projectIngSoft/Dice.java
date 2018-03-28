package projectIngSoft;

public class Dice {
    private final int value;
    private final Colour colour;

    public Dice(int value, Colour colour) {
        this.value = value;
        this.colour = colour;
    }

    public int getValue() {
        return value;
    }

    public Colour getColour() {
        return colour;
    }
    @Override
    public String toString() {
        String encoding = new String(Character.toChars(9855 + value));
        return encoding + " - " + colour;
    }
}
