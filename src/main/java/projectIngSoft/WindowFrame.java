package projectIngSoft;

public class WindowFrame {
    private final WindowPattern pattern;
    private Die[][] placedDice;

    public WindowFrame(WindowPattern pattern) {
        this.pattern = pattern;
        placedDice = new Die[pattern.getWidth()][pattern.getHeight()];
    }
}
