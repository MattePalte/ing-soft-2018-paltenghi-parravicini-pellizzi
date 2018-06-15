package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.Arrays;

public class RowShadeVariety extends PublicObjective {

    public RowShadeVariety(){
        super(  Settings.ObjectivesProperties.ROW_SHADE_VARIETY.getTitle(),
                Settings.ObjectivesProperties.ROW_SHADE_VARIETY.getDescription(),
                Settings.ObjectivesProperties.ROW_SHADE_VARIETY.getPoints(),
                Settings.ObjectivesProperties.ROW_SHADE_VARIETY.getPath());
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int ret = 0;
        int col;
        int row;
        int[] counter = new int [6];


        for (row = 0; row < placedDice.length ; row++) {
            for(col = 0; col < placedDice[0].length ;col++) {
                if(placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getValue() - 1] ++;
            }

            if(Arrays.stream(counter).filter(count -> count > 0).toArray().length >= placedDice[0].length)
                ret++;

            Arrays.fill(counter,0);
        }
        return ret;
    }
}
