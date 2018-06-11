package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.*;

/**
 * Specific implementation of a PublicObjective
 */
public class Diagonals extends PublicObjective {

    /**
     * Diagonals constructor. It takes information about the specific objective from
     * class Settings
     */
    public Diagonals(){
        super(  Settings.ObjectivesProperties.Diagonals.getTitle(),
                Settings.ObjectivesProperties.Diagonals.getDescription(),
                Settings.ObjectivesProperties.Diagonals.getPoints(),
                Settings.ObjectivesProperties.Diagonals.getPath());
    }

    /**
     * This method verifies if the objective is completed by the given player
     * and returns how many times he managed to complete it
     * @param window the player who is counting points on its window
     * @return how many times the condition to complete this objective is achieved
     */
    @Override
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
        for(rowIndex = height - 2; rowIndex > 0; rowIndex--){
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
