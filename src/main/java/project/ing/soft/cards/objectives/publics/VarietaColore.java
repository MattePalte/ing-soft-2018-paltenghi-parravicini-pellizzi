package project.ing.soft.cards.objectives.publics;

import project.ing.soft.Die;
import project.ing.soft.Colour;
import project.ing.soft.Player;

import java.util.*;

public class VarietaColore extends PublicObjective {

    public VarietaColore(){
        super("Varieta Colore", "Conta quanti set di dadi con 5 colori differenti hai composto sulla tua vetrata", 4);
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