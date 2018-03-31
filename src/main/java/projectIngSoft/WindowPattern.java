package projectIngSoft;

import java.io.BufferedReader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WindowPattern {
    private final int width;
    private final int height;
    private final WindowPatternCard front;
    private final WindowPatternCard rear;




    public int getWidth(){ return width; }

    public int getHeight(){
        return height;
    }

    public WindowPattern(Scanner in) throws Colour.ColorNotFoundException {
        String constraintRepr;
        String title;
        int difficulty;
        Constraint[][] constraints;

        title = in.nextLine();
        difficulty = in.nextInt();
        this.height = in.nextInt();
        this.width = in.nextInt();
        constraints = new Constraint[height][width];

        for(int row =0 ; row < height; row++) {
            for (int col = 0; col < width; col++) {
                constraintRepr = in.findInLine("[0-6][RYGBVW]");
                constraints[row][col] = new Constraint((int) constraintRepr.charAt(0) - (int) '0',
                        Colour.valueOf(constraintRepr.charAt(1)));
            }
        }

        this.front = new WindowPatternCard(new String(title), difficulty, constraints.clone());
        in.nextLine();
        title = in.nextLine();
        difficulty = in.nextInt();
        // These two lines are necessary to skip the width and the height saved in the string of the rear pattern
        in.nextInt();
        in.nextInt();
        constraints = new Constraint[this.height][this.width];

        for(int row =0 ; row < height; row++) {
            for (int col = 0; col < width; col++) {
                constraintRepr = in.findInLine("[0-6][RYGBVW]");
                constraints[row][col] = new Constraint((int) constraintRepr.charAt(0) - (int) '0',
                        Colour.valueOf(constraintRepr.charAt(1)));
            }
        }

        this.rear = new WindowPatternCard(new String(title), difficulty, constraints.clone());



    }

    @Override
    public String toString(){
        StringBuilder aBuilder = new StringBuilder();

        aBuilder.append("Front: \n");
        aBuilder.append(printFront());
        aBuilder.append("\nRear: \n");
        aBuilder.append(printRear());
        return new String(aBuilder);

    }

    public String printFront(){
        StringBuilder aBuilder = new StringBuilder();

        aBuilder.append(front.getTitle() + " "  + new String(new char[front.getDifficulty()]).replace("\0", "*")+"\n");

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < width ; j++) {
                aBuilder.append( front.getConstraintsMatrix()[i][j].getColour().ColourBackground(front.getConstraintsMatrix()[i][j].getValue()+""));
            }
            aBuilder.append("\n");

        }
        return new String(aBuilder);
    }

    public String printRear(){
        StringBuilder aBuilder = new StringBuilder();

        aBuilder.append(rear.getTitle()+" " +new String(new char[rear.getDifficulty()]).replace("\0", "*")+"\n");

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < width ; j++) {
                aBuilder.append( rear.getConstraintsMatrix()[i][j].getColour().ColourBackground(rear.getConstraintsMatrix()[i][j].getValue()+""));
            }
            aBuilder.append("\n");

        }
        return new String(aBuilder);
    }


}
