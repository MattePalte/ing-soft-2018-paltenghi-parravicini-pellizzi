package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.Arrays;

/**
 * Specific implementation of a PublicObjective
 */
public class RowColourVariety extends PublicObjective {

    /**
     * ROW_COLOUR_VARIETY constructor. It takes information about the specific objective from
     * class Settings
     */
    public RowColourVariety(){
        super(  Settings.ObjectivesProperties.ROW_COLOUR_VARIETY.getTitle(),
                Settings.ObjectivesProperties.ROW_COLOUR_VARIETY.getDescription(),
                Settings.ObjectivesProperties.ROW_COLOUR_VARIETY.getPoints(),
                Settings.ObjectivesProperties.ROW_COLOUR_VARIETY.getPath());
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
        int ret = 0;
        int row;
        int col;
        int[] counter = new int[Colour.validColours().size()];

        for (row = 0; row < placedDice.length; row++) {
            for(col = 0; col < placedDice[0].length ;col++) {
                if (placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getColour().ordinal()] ++;
            }
            if(Arrays.stream(counter).filter(count -> count > 0).toArray().length >= placedDice[0].length)
                ret ++;
            Arrays.fill(counter, 0);
        }
        return ret;
    }
}
