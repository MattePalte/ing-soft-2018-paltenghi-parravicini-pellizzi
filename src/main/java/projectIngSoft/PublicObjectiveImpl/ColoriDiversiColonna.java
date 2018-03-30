package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ColoriDiversiColonna extends PublicObjective {

    public ColoriDiversiColonna(){
        super("Colori Diversi - Colonna", "Hai formato colonne senza ripetere più volte lo stesso colore", 5);
    }

    public int checkCondition(WindowFrame window) {
        Die[][] placedDice = window.getPlacedDice();

        int ret = 0, row, col;
        int[] counter = new int[Colour.validColours().size()];

        for(col = 0; col < placedDice[0].length ;col++) {
            ret += 1;
            for (row = 0; row < placedDice.length; row++) {
                if (placedDice[row][col] == null)
                    continue;

                if( counter[placedDice[row][col].getColour().ordinal()] == 0) {
                    counter[placedDice[row][col].getColour().ordinal()] = 1;
                }else {
                    ret -= 1;
                    break;
                }
            }
            Arrays.fill(counter, 0);
        }
        return ret;
    }
}
