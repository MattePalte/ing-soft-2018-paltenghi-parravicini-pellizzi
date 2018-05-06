package project.ing.soft.cards.objectives.publics;

import project.ing.soft.Die;
import project.ing.soft.Player;

import java.util.Arrays;

public class SfumatureDiverseColonna extends PublicObjective {

    public SfumatureDiverseColonna(){
        super("Sfumature Diverse - Colonna", "Hai formato colonne senza ripetere piu volte lo stesso grado di sfumatura", 4,
                "objectives/public/30%/objectives-5.png");
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int col;
        int row;
        int[] counter = new int [6];
        int ret = 0;


        for(col = 0; col < placedDice[0].length ;col++) {
            for (row = 0; row < placedDice.length ; row++) {
                if(placedDice[row][col] == null)
                    continue;

                counter[placedDice[row][col].getValue() - 1] ++;
            }

            if(Arrays.stream(counter).filter(count -> count > 0).toArray().length >= placedDice.length)
                ret ++;
            Arrays.fill(counter, 0);

        }
        return ret;
    }
}
