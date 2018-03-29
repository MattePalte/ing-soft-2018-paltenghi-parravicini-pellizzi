package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.Die;
import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

import java.util.Arrays;

public class SfumatureDiverse extends PublicObjective {

    public SfumatureDiverse(){
        super("Sfumature Diverse", "Conta quanti set di sfumature (1,2,3,4,5,6) sei riuscito a comporre sulla tua vetrata", 5);
    }

    public int checkCondition(WindowFrame window) {
        Die[][] placedDice = window.getPlacedDice();
        int[] values = new int[6];

        for(int index = 0; index < 6; index++)
            values[index] = 0;
        for(Die[] row : placedDice)
            for(Die d : row){
                values[d.getValue() - 1]++;
            }
        Arrays.sort(values);
        return values[0];
    }
}
