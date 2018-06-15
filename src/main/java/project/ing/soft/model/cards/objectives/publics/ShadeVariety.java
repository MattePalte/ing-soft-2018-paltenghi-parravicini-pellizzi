package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.Arrays;

/**
 * Specific implementation of a PublicObjective
 */
public class ShadeVariety extends PublicObjective {

    /**
     * SHADE_VARIETY constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadeVariety(){
        super(  Settings.ObjectivesProperties.SHADE_VARIETY.getTitle(),
                Settings.ObjectivesProperties.SHADE_VARIETY.getDescription(),
                Settings.ObjectivesProperties.SHADE_VARIETY.getPoints(),
                Settings.ObjectivesProperties.SHADE_VARIETY.getPath());
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
        int[] values = new int[6];

        for (Die[] row : placedDice){
            for (Die d : row) {
                if (d != null) {
                    values[d.getValue() - 1]++;
                }
            }
        }

        return Arrays.stream(values).min().orElse(0);

    }
}
