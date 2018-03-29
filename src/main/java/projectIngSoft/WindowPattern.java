package projectIngSoft;

import java.io.BufferedReader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WindowPattern {
    private final int width;
    private final int height;
    private final Constraint[][] frontConstraintsMatrix;

    private final String frontTitle;

    private final int frontDifficulty;




    public int getWidth(){ return width; }

    public int getHeight(){
        return height;
    }

    public WindowPattern(Scanner in) throws Colour.ColorNotFoundException {

        this.frontTitle = in.nextLine();
        this.frontDifficulty = in.nextInt();
        this.height = in.nextInt();
        this.width = in.nextInt();
        frontConstraintsMatrix = new Constraint[this.height][this.width];

        for(int row =0 ; row < height; row++) {
            for (int col = 0; col < width; col++) {
                String constraintRepr = in.findInLine("[0-6][RYGBVW]");
                frontConstraintsMatrix[row][col] = new Constraint((int) constraintRepr.charAt(0) - (int) '0',
                        Colour.valueOf(constraintRepr.charAt(1)));
            }
        }



    }

    @Override
    public String toString(){
        StringBuilder aBuilder = new StringBuilder();
        aBuilder.append("Front\n"+this.frontTitle+" " +new String(new char[this.frontDifficulty]).replace("\0", "‧")+"\n");

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < width ; j++) {
                aBuilder.append( frontConstraintsMatrix[i][j].getColour().ColourBackground(frontConstraintsMatrix[i][j].getValue()+""));
            }
            aBuilder.append("\n");

        }
        return aBuilder.toString();
    }


}
