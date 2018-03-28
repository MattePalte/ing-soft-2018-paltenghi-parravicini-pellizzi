package projectIngSoft;

public class WindowFrame {
    private final WindowPattern pattern;
    private Die[][] placedDice;

    public WindowFrame(WindowPattern pattern) {
        this.pattern = pattern;
        placedDice = new Die[pattern.getWidth()][pattern.getHeight()];
        for(Die[] row: placedDice)
            for(Die d : row){
                d = new Die(Colour.BLANK);
            }
    }

    public Die[][] getPlacedDice(){
        return placedDice.clone();
    }

}
