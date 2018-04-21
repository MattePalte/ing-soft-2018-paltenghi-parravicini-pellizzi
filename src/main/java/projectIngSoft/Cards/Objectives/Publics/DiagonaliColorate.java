package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.*;
import projectIngSoft.Cards.WindowPattern;

import java.util.*;

public class DiagonaliColorate extends PublicObjective {

    public DiagonaliColorate(){
        super("Diagonali Colorate", "Conta il numero di dadi dello stesso colore posizionati diagonalmente l'uno rispetto all'altro", 1);
    }

    //TODO: avoid null dice
    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int height = placedDice.length;
        int width = placedDice[0].length;

        // This matrix is used to count how many times a die is counted. To avoid useless operations, this matrix's cells are initialized to -1
        int[][] counted = new int[height][width];
        int ret = 0;

        for(int row = 0; row < height; row ++)
            for(int col = 0; col < width; col ++)
                counted[row][col] = -1;

        for(int row = 0; row < height; row ++){
            for(int col = 0; col < width; col ++){
                if(placedDice[row][col] == null)
                    continue;
                ret += checkTL2BRDiagonal(placedDice, row, col, counted);
                ret += checkBL2TRDiagonal(placedDice, row, col, counted);
            }
        }

        for(int[] row : counted)
            for(int count : row)
                if(count > 0)
                    ret -= count;
        return ret;
    }

    private int checkBL2TRDiagonal(Die[][] placedDice, int row, int col, int[][] counted) {
        ArrayList<Die> diagonal = new ArrayList<>();
        Colour actualColour = placedDice[row][col].getColour();
        int ret;

        for(int j = 0; row - j >= 0; j++){
            if(placedDice[row - j][col + j] != null && placedDice[row - j][col + j].getColour().equals(actualColour)) {
                diagonal.add(placedDice[row - j][col + j]);
                counted[row - j][col + j]++;
            }
            else
                break;
        }
        if(diagonal.size() <= 1){
            ret = 0;
            counted[row][col] -= diagonal.size();
        }
        else
            ret = diagonal.size();

        return ret;
    }

    private int checkTL2BRDiagonal(Die[][] placedDice, int row, int col, int[][] counted){
        ArrayList<Die> diagonal = new ArrayList<>();
        Colour actualColour = placedDice[row][col].getColour();
        int ret;
        for(int j = 0; row + j < placedDice.length; j++){
            if(placedDice[row + j][col + j] != null && placedDice[row + j][col + j].getColour().equals(actualColour)) {
                diagonal.add(placedDice[row + j][col + j]);
                counted[row + j][col + j]++;
            }
            else
                break;
        }
        if(diagonal.size() <= 1){
            ret = 0;
            counted[row][col] -= diagonal.size();
        }
        else
            ret = diagonal.size();

        return ret;
    }
}
