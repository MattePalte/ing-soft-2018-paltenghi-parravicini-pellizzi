package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

public class LightShades extends PublicObjective {

    public LightShades(){
        super(  Settings.ObjectivesProperties.LightShades.getTitle(),
                Settings.ObjectivesProperties.LightShades.getDescription(),
                Settings.ObjectivesProperties.LightShades.getPoints(),
                Settings.ObjectivesProperties.LightShades.getPath());
    }

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
