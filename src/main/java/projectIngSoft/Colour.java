package projectIngSoft;

public enum Colour {
    RED, YELLOW, GREEN, BLUE, VIOLET, BLANK;

    @Override
    public String toString() {
        return "Colour{" + name()+ "}";
    }
}
