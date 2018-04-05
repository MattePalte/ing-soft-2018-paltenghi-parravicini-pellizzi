package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.*;

import java.util.Arrays;

public class SfumatureDiverseRiga extends PublicObjective {

    public SfumatureDiverseRiga(){
        super("Sfumature Diverse - Riga", "Hai formato righe senza ripetere piu volte lo stesso grado di sfumatura", 5);
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int ret = 0, col, row;
        int[] counter = new int [6];


        for (row = 0; row < placedDice[0].length ; row++) {
            ret+=1;
            for(col = 0; col < placedDice.length ;col++) {
                if(placedDice[row][col] == null)
                    continue;

                if(counter[placedDice[row][col].getValue() -1 ] == 0) {
                    counter[placedDice[row][col].getValue() - 1] = 1;
                }else{
                    ret -= 1;
                }
            }

            Arrays.fill(counter, 0);
        }
        return ret;
    }
}
