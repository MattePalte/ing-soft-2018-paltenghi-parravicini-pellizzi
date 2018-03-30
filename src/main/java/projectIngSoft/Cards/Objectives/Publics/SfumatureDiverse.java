package projectIngSoft.Cards.Objectives.Publics;

import projectIngSoft.Die;
import projectIngSoft.WindowFrame;

import java.util.Arrays;

public class SfumatureDiverse extends PublicObjective {

    public SfumatureDiverse(){
        super("Sfumature Diverse", "Conta quanti set di sfumature (1,2,3,4,5,6) sei riuscito a comporre sulla tua vetrata", 5);
    }

    public int checkCondition(WindowFrame window) {
        Die[][] placedDice = window.getPlacedDice();
        int[] values = new int[6];

        for (Die[] row : placedDice){
            for (Die d : row) {
                if (d != null) {
                    values[d.getValue() - 1]++;
                }
            }
        }

        return Arrays.stream(values).min().orElse(0);

    }
}
