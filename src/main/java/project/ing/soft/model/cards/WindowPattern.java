package project.ing.soft.model.cards;

import project.ing.soft.model.Colour;

import java.io.Serializable;
import java.util.Scanner;

public class WindowPattern implements Serializable {
    private final int width;
    private final int height;

    private final Constraint[][] constraintsMatrix;

    private final String title;
    private final int difficulty;

    /**
     * Create a pattern given the parameters of it
     * @param width nr of col
     * @param height nr of row
     * @param constraintsMatrix matrix of constraints
     * @param title title of the pattern
     * @param difficulty of the pattern
     */
    private WindowPattern(int width, int height, Constraint[][] constraintsMatrix, String title, int difficulty) {
        this.width = width;
        this.height = height;
        this.constraintsMatrix = constraintsMatrix;
        this.title = title;
        this.difficulty = difficulty;
    }


    public String getTitle() {
        return title;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * It returns the matrix of constraints of the pattern
     * @return matrix of constraints
     */
    public Constraint[][] getConstraintsMatrix() {
        return constraintsMatrix;
    }


    @Override
    public String toString() {
        StringBuilder aBuilder = new StringBuilder();
        String tmp;

        aBuilder.append(this.title)
                .append(" ")
                .append(new String(new char[this.difficulty]).replace("\0", "*"))
                .append("\n");

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                //if die was placed
                tmp = constraintsMatrix[r][c].toString();
                aBuilder.append(constraintsMatrix[r][c].getColour().colourBackground(tmp));

            }
            aBuilder.append("\n");
        }
        return new String(aBuilder);
    }

    /**
     * Given a Scanner representing a file resource,
     * it returns a pattern starting from the beginning of the file.
     * It reads two lines searching for:
     * - title
     * - difficulty
     * - nr of row of the pattern card
     * - nr of col of the pattern card
     * - and the encoding of the pattern card itself
     * @param in scanner representing a file
     * @return a window pattern
     * @throws Colour.ColorNotFoundException if the encoding of a color is incorrect
     */
    public static WindowPattern loadFromScanner(Scanner in) throws Colour.ColorNotFoundException {
        String constraintRepresentation;
        String title;
        int difficulty;
        int height;
        int width;
        Constraint[][] constraints;


        title = in.nextLine();
        difficulty = in.nextInt();
        height = in.nextInt();
        width = in.nextInt();
        constraints = new Constraint[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                constraintRepresentation = in.findInLine("[0-6][RYGBVW]");
                constraints[row][col] = Constraint.fromEncoding(constraintRepresentation);
            }
        }
        return new WindowPattern(width, height, constraints, title, difficulty);


    }

}





