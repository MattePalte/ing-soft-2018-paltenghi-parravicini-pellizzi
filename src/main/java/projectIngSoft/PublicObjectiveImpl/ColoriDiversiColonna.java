package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.ArrayList;

public class ColoriDiversiColonna extends PublicObjective {

    public ColoriDiversiColonna(){
        super("Colori Diversi - Colonna", "Hai formato colonne senza ripetere piu volte lo stesso colore", 5);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        ArrayList<Colour> diffColours = new ArrayList<Colour>();
        WindowPattern pattern = window.getPattern();
        Die[][] placedDice = window.getPlacedDice();
        int col = 0;
        int row = 0;
        int ret = 0;


        for(col = 0; col < pattern.getWidth();col++) {
            for (row = 0; row < pattern.getHeight(); row++) {
                if (!diffColours.contains(placedDice[row][col]))
                    diffColours.add(placedDice[row][col].getColour());
            }
            if(diffColours.size() == pattern.getHeight())
                ret++;
        }
        return ret;
    }
}
