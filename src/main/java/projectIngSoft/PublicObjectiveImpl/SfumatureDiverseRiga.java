package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.ArrayList;

public class SfumatureDiverseRiga extends PublicObjective {

    public SfumatureDiverseRiga(){
        super("Sfumature Diverse - Riga", "Hai formato righe senza ripetere piu volte lo stesso grado di sfumatura", 5);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        ArrayList<Integer> diffValues = new ArrayList<Integer>();
        WindowPattern pattern = window.getPattern();
        Die[][] placedDice = window.getPlacedDice();
        int col = 0;
        int row = 0;
        int ret = 0;


        for(row = 0; row < pattern.getHeight(); row++) {
            for (col = 0; col < pattern.getWidth(); col++) {
                if (!diffValues.contains(placedDice[row][col].getValue()))
                    diffValues.add(placedDice[row][col].getValue());
            }
            if(diffValues.size() == pattern.getWidth())
                ret++;
        }
        return ret;
    }
}
