package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.Settings;
import project.ing.soft.model.Die;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;

import java.util.*;

public class ColourVariety extends PublicObjective {

    public ColourVariety(){
        super(  Settings.ObjectivesProperties.ColourVariety.getTitle(),
                Settings.ObjectivesProperties.ColourVariety.getDescription(),
                Settings.ObjectivesProperties.ColourVariety.getPoints(),
                Settings.ObjectivesProperties.ColourVariety.getPath());
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();

        int row;
        int col;
        int[] counter = new int[Colour.validColours().size()];

        for(col = 0; col < placedDice[0].length ;col++) {
            for (row = 0; row < placedDice.length; row++) {
                if (placedDice[row][col] != null) {
                    counter[placedDice[row][col].getColour().ordinal()] ++;
                }
            }
        }


        return Arrays.stream(counter).min().orElse(0);
    }
}