package project.ing.soft.cards.objectives.publics;

import project.ing.soft.Colour;
import project.ing.soft.Die;
import project.ing.soft.Player;

import java.util.*;

public class DiagonaliColorate extends PublicObjective {

    public DiagonaliColorate(){
        super("Diagonali Colorate", "Conta il numero di dadi dello stesso colore posizionati diagonalmente l'uno rispetto all'altro", 1);
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int height = placedDice.length;
        int width = placedDice[0].length;

        int ret = 0;
        int rowIndex = 0;
        int colIndex = 0;

        rowIndex = 0;
        for(colIndex = width - 2; colIndex >= 0; colIndex--){
            ret += checkTL2BRDiagonal(placedDice, rowIndex, colIndex);
        }

        colIndex = 0;
        for(rowIndex = 1; rowIndex <= height - 2; rowIndex++){
            ret += checkTL2BRDiagonal(placedDice, rowIndex, colIndex);
        }

        rowIndex = height - 1;
        for(colIndex = width - 2; colIndex >= 0; colIndex--){
            ret += checkBL2TRDiagonal(placedDice, rowIndex, colIndex);
        }

        colIndex = 0;
        for(rowIndex = height - 1; rowIndex > 0; rowIndex--){
            ret += checkBL2TRDiagonal(placedDice,rowIndex, colIndex);
        }

        return ret;
    }

    // Starting from placedDice[row][col], visits all the cells on the bottom-left to top-right diagonal
    // It returns the number dice with the same colour placed sequentially on the diagonal
    private int checkBL2TRDiagonal(Die[][] placedDice, int row, int col) {
        ArrayList<Die> diagonal = new ArrayList<>();
        Colour actualColour = null;
        int ret = 0;

        // Visiting diagonal
        for(int j = 0; row - j >= 0 && col + j < placedDice[0].length; j++){
            if(placedDice[row - j][col + j] == null)
                continue;
            if(placedDice[row - j][col + j].getColour().equals(actualColour)) {
                diagonal.add(placedDice[row - j][col + j]);
            }
            else { // If this die has a different colour from the actual...
                actualColour = placedDice[row - j][col + j].getColour();
                ret += diagonal.size() > 1 ? diagonal.size() : 0;
                diagonal.clear();
                diagonal.add(placedDice[row - j][col + j]);
            }
        }

        ret += diagonal.size() > 1 ? diagonal.size() : 0;
        return ret;
    }

    // Starting from placedDice[row][col], visits all the cells on the top-left to bottom-right diagonal
    // It returns the number dice with the same colour placed sequentially on the diagonal
    private int checkTL2BRDiagonal(Die[][] placedDice, int row, int col){
        ArrayList<Die> diagonal = new ArrayList<>();
        Colour actualColour = null;
        int ret = 0;

        // Visiting diagonal
        for(int j = 0; row + j < placedDice.length && col + j < placedDice[0].length; j++){
            if(placedDice[row + j][col + j] == null)
                continue;
            if(placedDice[row + j][col + j].getColour().equals(actualColour)) {
                diagonal.add(placedDice[row + j][col + j]);
            }
            else { // If this die is null or it has a different colour from the actual...
                actualColour = placedDice[row + j][col + j].getColour();
                ret += diagonal.size() > 1 ? diagonal.size() : 0;
                diagonal.clear();
                diagonal.add(placedDice[row + j][col + j]);
            }
        }

        ret += diagonal.size() > 1 ? diagonal.size() : 0;
        return ret;

    }
}
