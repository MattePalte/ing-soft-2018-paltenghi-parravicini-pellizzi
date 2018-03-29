package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.ArrayList;

public class ColoriDiversiRiga extends PublicObjective {

    public ColoriDiversiRiga(){
        super("Colori Diversi - Riga", "Hai formato righe senza ripetere piu volte lo stesso colore", 6);
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


        for(row = 0; row < pattern.getHeight(); row++) {
            for (col = 0; col < pattern.getWidth(); col++) {
                if (!diffColours.contains(placedDice[row][col].getColour()))
                    diffColours.add(placedDice[row][col].getColour());
            }
            if(diffColours.size() == pattern.getWidth())
                ret++;
        }
        return ret;
    }
}
