package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.Arrays;

public class ShadeVariety extends PublicObjective {

    public ShadeVariety(){
        super(  Settings.ObjectivesProperties.ShadeVariety.getTitle(),
                Settings.ObjectivesProperties.ShadeVariety.getDescription(),
                Settings.ObjectivesProperties.ShadeVariety.getPoints(),
                Settings.ObjectivesProperties.ShadeVariety.getPath());
    }

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
