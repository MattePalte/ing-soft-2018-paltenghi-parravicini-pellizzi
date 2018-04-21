package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.*;

import java.util.Arrays;

public class ColoriDiversiRiga extends PublicObjective {

    public ColoriDiversiRiga(){
        super("Colori Diversi - Riga", "Hai formato righe senza ripetere piu volte lo stesso colore", 6);
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();

        int row, col;
        int[] counter = new int[Colour.validColours().size()];

        for (row = 0; row < placedDice.length; row++) {
            for(col = 0; col < placedDice[0].length ;col++) {
                if (placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getColour().ordinal()] ++;
            }
        }
        return Arrays.stream(counter).filter(count -> count > 0).min().orElse(0);
    }
}
