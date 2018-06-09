package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Die;
import project.ing.soft.model.Player;

import java.util.Arrays;

public class RowColourVariety extends PublicObjective {

    public RowColourVariety(){
        super(  Settings.ObjectivesProperties.RowColourVariety.getTitle(),
                Settings.ObjectivesProperties.RowColourVariety.getDescription(),
                Settings.ObjectivesProperties.RowColourVariety.getPoints(),
                Settings.ObjectivesProperties.RowColourVariety.getPath());
    }

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
