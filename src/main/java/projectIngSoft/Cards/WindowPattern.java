package projectIngSoft.Cards;

import projectIngSoft.Colour;
import projectIngSoft.Die;

import java.util.Scanner;

public class WindowPattern {
    private final int width;
    private final int height;

    private final Constraint[][] ConstraintsMatrix;

    private final String Title;
    private final int Difficulty;

    private WindowPattern(int width, int height, Constraint[][] constraintsMatrix, String title, int difficulty) {
        this.width = width;
        this.height = height;
        ConstraintsMatrix = constraintsMatrix;
        Title = title;
        Difficulty = difficulty;
    }


    public String getTitle() {
        return Title;
    }

    public int getDifficulty() {
        return Difficulty;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Constraint[][] getConstraintsMatrix() {
        return ConstraintsMatrix;
    }


    @Override
    public String toString() {
        StringBuilder aBuilder = new StringBuilder();
        String tmp;

        aBuilder.append(this.Title + " " + new String(new char[this.Difficulty]).replace("\0", "*") + "\n");

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                //if die was placed
                tmp = ConstraintsMatrix[r][c].toString();
                aBuilder.append(ConstraintsMatrix[r][c].getColour().ColourBackground(tmp));

            }
            aBuilder.append("\n");
        }
        return new String(aBuilder);
    }

    public static WindowPattern loadFromScanner(Scanner in) throws Colour.ColorNotFoundException {
        String constraintRepr;
        String title;
        int difficulty, height, width;
        Constraint[][] constraints;


        title = in.nextLine();
        difficulty = in.nextInt();
        height = in.nextInt();
        width = in.nextInt();
        constraints = new Constraint[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                constraintRepr = in.findInLine("[0-6][RYGBVW]");
                constraints[row][col] = Constraint.fromEncoding(constraintRepr);
            }
        }
        return new WindowPattern(width, height, constraints, title, difficulty);


    }

}





