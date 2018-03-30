package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.ArrayList;
import java.util.Arrays;

public class SfumatureDiverseColonna extends PublicObjective {

    public SfumatureDiverseColonna(){
        super("Sfumature Diverse - Colonna", "Hai formato colonne senza ripetere piu volte lo stesso grado di sfumatura", 4);
    }

    public int checkCondition(WindowFrame window) {
        Die[][] placedDice = window.getPlacedDice();
        int ret = 0, col, row;
        int[] counter = new int [6];


        for(col = 0; col < placedDice.length ;col++) {
            ret+=1;
            for (row = 0; row < placedDice[0].length ; row++) {
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
