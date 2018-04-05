package projectIngSoft.Cards;

import projectIngSoft.Colour;
import projectIngSoft.Constraint;

import java.util.Scanner;

public class WindowPattern {
    private final int width;
    private final int height;

    private final Constraint[][] ConstraintsMatrix;

    private final String Title;
    private final int Difficulty;


    public WindowPattern(Scanner in) throws Colour.ColorNotFoundException {
        String constraintRepr;
        String title;
        int difficulty;
        Constraint[][] constraints;


        this.Title = in.nextLine();
        this.Difficulty = in.nextInt();
        this.height = in.nextInt();
        this.width = in.nextInt();
        ConstraintsMatrix = new Constraint[this.height][this.width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                constraintRepr = in.findInLine("[0-6][RYGBVW]");
                ConstraintsMatrix[row][col] = new Constraint((int) constraintRepr.charAt(0) - (int) '0',

                        Colour.valueOf(constraintRepr.charAt(1)));
            }
        }
        in.nextLine();
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


    @Override
    public String toString() {
        StringBuilder aBuilder = new StringBuilder();

        aBuilder.append(this.Title + " " + new String(new char[this.Difficulty]).replace("\0", "‧") + "\n");

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                aBuilder.append(ConstraintsMatrix[i][j].getColour().ColourBackground(ConstraintsMatrix[i][j].getValue() + ""));


            }
            aBuilder.append("\n");
        }
        return new String(aBuilder);
    }
}





