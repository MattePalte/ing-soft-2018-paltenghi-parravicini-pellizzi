package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class ColoriDiversiColonna extends PublicObjective {

    public ColoriDiversiColonna(){
        super("Colori Diversi - Colonna", "Hai formato colonne senza ripetere piu volte lo stesso colore", 5);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
