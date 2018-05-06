package project.ing.soft.cards.objectives.publics;

import project.ing.soft.Die;
import project.ing.soft.Player;

public class SfumatureMedie extends PublicObjective{

    public SfumatureMedie(){
        super("Sfumature Medie", "Conta il numero di coppie di dadi (3,4) presenti sulla tua vetrata", 2,
                "objectives/public/30%/objectives-7.png");
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int[] values = new int[2];
        int actualDieValue;

        values[0] = 0;
        values[1] = 0;
        for (Die[] row : placedDice){
            for (Die d : row) {
                if (d != null) {
                    actualDieValue = d.getValue();
                    if (actualDieValue == 3 || actualDieValue == 4)
                        values[actualDieValue - 3]++;
                }
            }
        }

        return Integer.min(values[0],values[1]);
    }
}
