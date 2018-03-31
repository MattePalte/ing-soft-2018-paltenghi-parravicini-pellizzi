package projectIngSoft;

import java.io.BufferedReader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WindowPattern {
    private final int width;
    private final int height;
    private final Constraint[][] frontConstraintsMatrix;
    private final Constraint[][] rearConstraintsMatrix;

    private final String frontTitle;
    private final String rearTitle;

    private final int frontDifficulty;
    private final int rearDifficulty;




    public int getWidth(){ return width; }

    public int getHeight(){
        return height;
    }

    public Constraint[][] getFrontConstraints(){
        return frontConstraintsMatrix.clone();
    }

    public Constraint[][] getRearConstraints(){
        return rearConstraintsMatrix.clone();
    }

    public String getFrontTitle(){
        return new String(frontTitle);
    }

    public String getRearTitle(){
        return new String(rearTitle);
    }

    public int getFrontDifficulty(){
        return frontDifficulty;
    }

    public int getRearDifficulty(){
        return rearDifficulty;
    }

    public WindowPattern(Scanner in) throws Colour.ColorNotFoundException {
        String constraintRepr;

        this.frontTitle = in.nextLine();
        this.frontDifficulty = in.nextInt();
        this.height = in.nextInt();
        this.width = in.nextInt();
        frontConstraintsMatrix = new Constraint[this.height][this.width];

        for(int row =0 ; row < height; row++) {
            for (int col = 0; col < width; col++) {
                constraintRepr = in.findInLine("[0-6][RYGBVW]");
                frontConstraintsMatrix[row][col] = new Constraint((int) constraintRepr.charAt(0) - (int) '0',
                        Colour.valueOf(constraintRepr.charAt(1)));
            }
        }

        in.nextLine();
        this.rearTitle = in.nextLine();
        this.rearDifficulty = in.nextInt();
        // These two lines are necessary to skip the width and the height saved in the string of the rear pattern
        in.nextInt();
        in.nextInt();
        rearConstraintsMatrix = new Constraint[this.height][this.width];

        for(int row =0 ; row < height; row++) {
            for (int col = 0; col < width; col++) {
                constraintRepr = in.findInLine("[0-6][RYGBVW]");
                rearConstraintsMatrix[row][col] = new Constraint((int) constraintRepr.charAt(0) - (int) '0',
                        Colour.valueOf(constraintRepr.charAt(1)));
            }
        }



    }

    @Override
    public String toString(){
        StringBuilder aBuilder = new StringBuilder();
        aBuilder.append("Front: \n"+this.frontTitle+" " +new String(new char[this.frontDifficulty]).replace("\0", "*")+"\n");

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < width ; j++) {
                aBuilder.append( frontConstraintsMatrix[i][j].getColour().ColourBackground(frontConstraintsMatrix[i][j].getValue()+""));
            }
            aBuilder.append("\n");

        }

        aBuilder.append("\nRear: \n"+this.rearTitle+" " +new String(new char[this.rearDifficulty]).replace("\0", "*")+"\n");

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < width ; j++) {
                aBuilder.append( rearConstraintsMatrix[i][j].getColour().ColourBackground(rearConstraintsMatrix[i][j].getValue()+""));
            }
            aBuilder.append("\n");

        }

        return aBuilder.toString();
    }

    public void printFront(){
        StringBuilder aBuilder = new StringBuilder();

        aBuilder.append(frontTitle + " "  + new String(new char[frontDifficulty]).replace("\0", "*")+"\n");

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < width ; j++) {
                aBuilder.append( frontConstraintsMatrix[i][j].getColour().ColourBackground(frontConstraintsMatrix[i][j].getValue()+""));
            }
            aBuilder.append("\n");

        }
        System.out.println(aBuilder);
    }

    public void printRear(){
        StringBuilder aBuilder = new StringBuilder();

        aBuilder.append(rearTitle+" " +new String(new char[rearDifficulty]).replace("\0", "*")+"\n");

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < width ; j++) {
                aBuilder.append( rearConstraintsMatrix[i][j].getColour().ColourBackground(rearConstraintsMatrix[i][j].getValue()+""));
            }
            aBuilder.append("\n");

        }
        System.out.println(aBuilder);
    }


}
