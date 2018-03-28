package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class ColoriDiversiRiga extends PublicObjective {

    public ColoriDiversiRiga(){
        super("Colori Diversi - Riga", "Hai formato righe senza ripetere piu volte lo stesso colore", 6);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
