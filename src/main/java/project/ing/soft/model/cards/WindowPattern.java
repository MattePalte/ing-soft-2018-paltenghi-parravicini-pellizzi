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





