package projectIngSoft;

public class WindowFrame {
    private final WindowPattern pattern;
    private Die[][] placedDice;

    public WindowFrame(WindowPattern pattern, int width, int height) {
        this.pattern = pattern;
        placedDice = new Die[width][height];
    }
}
