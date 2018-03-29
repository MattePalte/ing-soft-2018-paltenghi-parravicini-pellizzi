package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.Die;
import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class SfumatureMedie extends PublicObjective{

    public SfumatureMedie(){
        super("Sfumature Medie", "Conta il numero di coppie di dadi (3,4) presenti sulla tua vetrata", 2);
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
                if(actualValue == 3 || actualValue == 4)
                    values[actualValue - 3]++;
            }
        return Integer.min(values[0],values[1]);
    }
}
