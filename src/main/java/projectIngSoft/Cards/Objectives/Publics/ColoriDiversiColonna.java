package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.*;

import java.util.Arrays;

public class ColoriDiversiColonna extends PublicObjective {

    public ColoriDiversiColonna(){
        super("Colori Diversi - Colonna", "Hai formato colonne senza ripetere più volte lo stesso colore", 5);
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int ret = 0;
        int row, col;
        int[] counter = new int[Colour.validColours().size()];

        for(col = 0; col < placedDice[0].length ;col++) {
            for (row = 0; row < placedDice.length; row++) {
                if (placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getColour().ordinal()] ++;
            }

            if(Arrays.stream(counter).filter(count -> count > 0).toArray().length >= placedDice.length)
                ret ++;
            Arrays.fill(counter,0);
        }
        return ret;
    }
}
