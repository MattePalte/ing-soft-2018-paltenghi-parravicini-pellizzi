package project.ing.soft.cards.objectives.publics;

import project.ing.soft.Colour;
import project.ing.soft.Die;
import project.ing.soft.Player;

import java.util.Arrays;

public class ColoriDiversiRiga extends PublicObjective {

    public ColoriDiversiRiga(){
        super("Colori Diversi - Riga", "Hai formato righe senza ripetere piu volte lo stesso colore", 6);
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
