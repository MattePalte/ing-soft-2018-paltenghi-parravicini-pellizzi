package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

import java.util.*;

/**
 * Specific implementation of a PublicObjective
 */
public class ColourVariety extends PublicObjective {

    /**
     * COLOUR_VARIETY constructor. It takes information about the specific objective from
     * class Settings
     */
    public ColourVariety(){
        super(  Settings.ObjectivesProperties.COLOUR_VARIETY.getTitle(),
                Settings.ObjectivesProperties.COLOUR_VARIETY.getDescription(),
                Settings.ObjectivesProperties.COLOUR_VARIETY.getPoints(),
                Settings.ObjectivesProperties.COLOUR_VARIETY.getPath());
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

        int row;
        int col;
        int[] counter = new int[Colour.validColours().size()];

        for(col = 0; col < placedDice[0].length ;col++) {
            for (row = 0; row < placedDice.length; row++) {
                if (placedDice[row][col] != null) {
                    counter[placedDice[row][col].getColour().ordinal()] ++;
                }
            }
        }


        return Arrays.stream(counter).min().orElse(0);
    }
}