package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

public class DarkShades extends PublicObjective {

    public DarkShades(){
        super(  Settings.ObjectivesProperties.DarkShades.getTitle(),
                Settings.ObjectivesProperties.DarkShades.getDescription(),
                Settings.ObjectivesProperties.DarkShades.getPoints(),
                Settings.ObjectivesProperties.DarkShades.getPath());
    }

    @Override
    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int[] values = new int[2];
        int actualValue;

        values[0] = 0;
        values[1] = 0;
        for(Die[] row : placedDice) {
            for (Die d : row) {
                if (d != null) {
                    actualValue = d.getValue();
                    if (actualValue == 5 || actualValue == 6)
                        values[actualValue - 5]++;
                }
            }
        }

        return Integer.min(values[0],values[1]);
    }
}
