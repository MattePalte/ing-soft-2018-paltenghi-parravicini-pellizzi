package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.*;

import java.util.Arrays;

public class SfumatureDiverseRiga extends PublicObjective {

    public SfumatureDiverseRiga(){
        super("Sfumature Diverse - Riga", "Hai formato righe senza ripetere piu volte lo stesso grado di sfumatura", 5);
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int col, row;
        int[] counter = new int [6];


        for (row = 0; row < placedDice.length ; row++) {
            for(col = 0; col < placedDice[0].length ;col++) {
                if(placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getValue() - 1] ++;
            }

        }
        // Deleting elements eventually remained to 0 and taking the minimum value of the counter array
        return Arrays.stream(counter).filter(count -> count > 0).min().orElse(0);
    }
}
