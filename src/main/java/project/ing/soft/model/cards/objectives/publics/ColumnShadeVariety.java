package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.Arrays;

public class ColumnShadeVariety extends PublicObjective {

    public ColumnShadeVariety(){
        super(  Settings.ObjectivesProperties.ColumnShadeVariety.getTitle(),
                Settings.ObjectivesProperties.ColumnShadeVariety.getDescription(),
                Settings.ObjectivesProperties.ColumnShadeVariety.getPoints(),
                Settings.ObjectivesProperties.ColumnShadeVariety.getPath());
    }

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
