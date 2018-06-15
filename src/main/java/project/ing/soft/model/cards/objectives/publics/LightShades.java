package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

/**
 * Specific implementation of a PublicObjective
 */
public class LightShades extends PublicObjective {

    /**
     * LIGHT_SHADES constructor. It takes information about the specific objective from
     * class Settings
     */
    public LightShades(){
        super(  Settings.ObjectivesProperties.LIGHT_SHADES.getTitle(),
                Settings.ObjectivesProperties.LIGHT_SHADES.getDescription(),
                Settings.ObjectivesProperties.LIGHT_SHADES.getPoints(),
                Settings.ObjectivesProperties.LIGHT_SHADES.getPath());
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
        int[] values = new int[2];
        int actualDieValue;

        values[0] = 0;
        values[1] = 0;

        for(Die[] row : placedDice) {
            for (Die d : row) {
                if (d != null) {
                    actualDieValue = d.getValue();
                    if (actualDieValue == 1 || actualDieValue == 2)
                        values[actualDieValue - 1]++;
                }
            }
        }

        return Integer.min(values[0],values[1]);
    }
}
