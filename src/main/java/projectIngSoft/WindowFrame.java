package projectIngSoft;

public class WindowFrame {
    private final WindowPattern pattern;
    private Die[][] placedDice;
    private boolean isWindowPatternFlipped;

    public WindowFrame(WindowPattern pattern, boolean isWindowPatternFlipped ) {
        this.pattern = pattern;
        this.isWindowPatternFlipped = isWindowPatternFlipped;
        placedDice = new Die[pattern.getWidth()][pattern.getHeight()];

    }

    public Die[][] getPlacedDice(){
        return placedDice.clone();
    }

    public WindowPattern getPattern(){
        return pattern;
    }
}
