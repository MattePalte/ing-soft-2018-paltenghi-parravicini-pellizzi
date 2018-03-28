package projectIngSoft;

public class WindowPattern {
    private final int width;
    private final int height;
    private final Constraint[][] constraintsMatrix;

    public WindowPattern(int width, int height) {
        this.width = width;
        this.height = height;
        constraintsMatrix = new Constraint[width][height];

    }
}
