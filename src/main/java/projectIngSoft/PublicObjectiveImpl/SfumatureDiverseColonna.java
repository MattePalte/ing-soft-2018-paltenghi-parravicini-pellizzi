package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.ArrayList;

public class SfumatureDiverseColonna extends PublicObjective {

    public SfumatureDiverseColonna(){
        super("Sfumature Diverse - Colonna", "Hai formato colonne senza ripetere piu volte lo stesso grado di sfumatura", 4);
    }

    public int checkCondition(WindowFrame window) {
        ArrayList<Integer> diffValues = new ArrayList<Integer>();
        WindowPattern pattern = window.getPattern();
        Die[][] placedDice = window.getPlacedDice();
        int col = 0;
        int row = 0;
        int ret = 0;


        for(col = 0; col < pattern.getWidth();col++) {
            for (row = 0; row < pattern.getHeight(); row++) {
                if (!diffValues.contains(placedDice[row][col].getValue()))
                    diffValues.add(placedDice[row][col].getValue());
            }
            if(diffValues.size() == pattern.getHeight())
                ret++;
        }
        return ret;
    }
}
