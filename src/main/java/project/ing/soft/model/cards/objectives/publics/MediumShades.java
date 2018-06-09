package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

/**
 * Specific implementation of a PublicObjective
 */
public class MediumShades extends PublicObjective{

    /**
     * MediumShades constructor. It takes information about the specific objective from
     * class Settings
     */
    public MediumShades(){
        super(  Settings.ObjectivesProperties.MediumShades.getTitle(),
                Settings.ObjectivesProperties.MediumShades.getDescription(),
                Settings.ObjectivesProperties.MediumShades.getPoints(),
                Settings.ObjectivesProperties.MediumShades.getPath());
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
        for (Die[] row : placedDice){
            for (Die d : row) {
                if (d != null) {
                    actualDieValue = d.getValue();
                    if (actualDieValue == 3 || actualDieValue == 4)
                        values[actualDieValue - 3]++;
                }
            }
        }

        return Integer.min(values[0],values[1]);
    }
}
