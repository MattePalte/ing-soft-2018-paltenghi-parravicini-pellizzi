package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.Arrays;

/**
 * Specific implementation of a PublicObjective
 */
public class ColumnShadeVariety extends PublicObjective {

    /**
     * ColumnShadeVariety constructor. It takes information about the specific objective from
     * class Settings
     */
    public ColumnShadeVariety(){
        super(  Settings.ObjectivesProperties.ColumnShadeVariety.getTitle(),
                Settings.ObjectivesProperties.ColumnShadeVariety.getDescription(),
                Settings.ObjectivesProperties.ColumnShadeVariety.getPoints(),
                Settings.ObjectivesProperties.ColumnShadeVariety.getPath());
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
        int col;
        int row;
        int[] counter = new int [6];
        int ret = 0;


        for(col = 0; col < placedDice[0].length ;col++) {
            for (row = 0; row < placedDice.length ; row++) {
                if(placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getValue() - 1] ++;
            }

            if(Arrays.stream(counter).filter(count -> count > 0).toArray().length >= placedDice.length)
                ret ++;
            Arrays.fill(counter, 0);

        }
        return ret;
    }
}
