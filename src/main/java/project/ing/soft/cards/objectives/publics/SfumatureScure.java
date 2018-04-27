package project.ing.soft.cards.objectives.publics;

import project.ing.soft.Die;
import project.ing.soft.Player;

public class SfumatureScure extends PublicObjective {

    public SfumatureScure(){
        super("Sfumature Scure", "Conta il numero di coppie di dadi (5,6) presenti sulla tua vetrata", 2);
    }

    @Override
    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int[] values = new int[2];
        int actualValue;

        values[0] = 0;
        values[1] = 0;
        for(Die[] row : placedDice) {
            for (Die d : row) {
                if (d != null) {
                    actualValue = d.getValue();
                    if (actualValue == 5 || actualValue == 6)
                        values[actualValue - 5]++;
                }
            }
        }

        return Integer.min(values[0],values[1]);
    }
}
