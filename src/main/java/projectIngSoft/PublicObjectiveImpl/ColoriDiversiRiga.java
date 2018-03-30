package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ColoriDiversiRiga extends PublicObjective {

    public ColoriDiversiRiga(){
        super("Colori Diversi - Riga", "Hai formato righe senza ripetere piu volte lo stesso colore", 6);
    }

    public int checkCondition(WindowFrame window) {
        Die[][] placedDice = window.getPlacedDice();

        int ret = 0, row, col;
        int[] counter = new int[Colour.validColours().size()];

        for (row = 0; row < placedDice.length; row++) {
            ret += 1;
            for(col = 0; col < placedDice[0].length ;col++) {
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
