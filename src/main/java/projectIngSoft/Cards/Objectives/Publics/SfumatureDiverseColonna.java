package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.*;

import java.util.Arrays;

public class SfumatureDiverseColonna extends PublicObjective {

    public SfumatureDiverseColonna(){
        super("Sfumature Diverse - Colonna", "Hai formato colonne senza ripetere piu volte lo stesso grado di sfumatura", 4);
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int col, row;
        int[] counter = new int [6];


        for(col = 0; col < placedDice[0].length ;col++) {
            for (row = 0; row < placedDice.length ; row++) {
                if(placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getValue() - 1] ++;
            }

        }
        return Arrays.stream(counter).filter(count -> count > 0).min().orElse(0);
    }
}
