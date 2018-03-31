package projectIngSoft;

public class WindowPatternCard {
    private final Constraint[][] constraintsMatrix;
    private final String title;
    private final int difficulty;

    public WindowPatternCard(String title, int difficulty, Constraint[][] constraints){
        this.title = title;
        this.difficulty = difficulty;
        this.constraintsMatrix = constraints;
    }

    public Constraint[][] getConstraintsMatrix() {
        return constraintsMatrix.clone();
    }

    public String getTitle(){
        return new String(title);
    }

    public int getDifficulty(){
        return difficulty;
    }
}
