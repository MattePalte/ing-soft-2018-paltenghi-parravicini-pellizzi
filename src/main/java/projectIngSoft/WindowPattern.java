package projectIngSoft;

import java.io.BufferedReader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WindowPattern {
    private final int width;
    private final int height;
    private final Constraint[][] frontConstraintsMatrix;
    private final Constraint[][] rearConstraintMatrix;

    public WindowPattern(int width, int height) {
        this.width = width;
        this.height = height;
        frontConstraintsMatrix = new Constraint[width][height];
        rearConstraintMatrix = new Constraint[width][height];
    }

    public int getWidth(){ return width; }

    public int getHeight(){
        return height;
    }

    public WindowPattern(Scanner in) throws Colour.ColorNotFoundException {


        this.height = in.nextInt();
        this.width = in.nextInt();
        frontConstraintsMatrix = new Constraint[this.height][this.width];
        rearConstraintMatrix   = new Constraint[this.height][this.width];


        for(int row =0 ; row < height; row++)
            for(int col=0; col < width; col++){
                String constraintRepr =  in.findInLine("[0-6][RYGBVW]");
                frontConstraintsMatrix[row][col] = new Constraint((int)constraintRepr.charAt(0) - (int) '0',
                                                                    Colour.valueOf(constraintRepr.charAt(1)) );
            }



    }


}
