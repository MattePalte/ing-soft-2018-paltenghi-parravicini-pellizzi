package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.Die;
import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class SfumatureChiare extends PublicObjective {

    public SfumatureChiare(){
        super("Sfumature Chiare","Conta il numero di coppie di dadi (1,2) presenti sulla tua vetrata", 2);
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
                if(actualValue == 1 || actualValue == 2)
                    values[actualValue - 1]++;
            }
        return Integer.min(values[0],values[1]);
    }
}
