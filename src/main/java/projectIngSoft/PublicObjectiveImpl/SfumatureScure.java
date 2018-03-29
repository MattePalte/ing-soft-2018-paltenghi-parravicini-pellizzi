package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.Die;
import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class SfumatureScure extends PublicObjective {

    public SfumatureScure(){
        super("Sfumature Scure", "Conta il numero di coppie di dadi (5,6) presenti sulla tua vetrata", 2);
    }

    public int checkCondition(WindowFrame window) {
        Die[][] placedDice = window.getPlacedDice();
        int[] values = new int[2];
        int actualValue;

        values[0] = 0;
        values[1] = 0;
        for(Die[] row : placedDice)
            for(Die d : row){
                actualValue = d.getValue();
                if(actualValue == 5 || actualValue == 6)
                    values[actualValue - 5]++;
            }
        return Integer.min(values[0],values[1]);
    }
}
