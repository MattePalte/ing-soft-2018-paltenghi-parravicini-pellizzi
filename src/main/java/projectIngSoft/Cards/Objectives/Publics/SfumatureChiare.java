package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.Die;
import projectIngSoft.Player;

public class SfumatureChiare extends PublicObjective {

    public SfumatureChiare(){
        super("Sfumature Chiare","Conta il numero di coppie di dadi (1,2) presenti sulla tua vetrata", 2);
    }

    public int checkCondition(Player window) {
        Die[][] placedDice = window.getPlacedDice();
        int[] values = new int[2];
        int actualDieValue;

        values[0] = 0;
        values[1] = 0;

        for(Die[] row : placedDice) {
            for (Die d : row) {
                if (d != null) {
                    actualDieValue = d.getValue();
                    if (actualDieValue == 1 || actualDieValue == 2)
                        values[actualDieValue - 1]++;
                }
            }
        }

        return Integer.min(values[0],values[1]);
    }
}
