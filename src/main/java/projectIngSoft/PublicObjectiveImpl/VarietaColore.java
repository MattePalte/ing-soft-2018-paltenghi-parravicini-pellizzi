package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowPattern;

public class VarietaColore extends PublicObjective {

    public VarietaColore(){
        super("Varieta Colore", "Conta quanti set di dadi con 5 colori differenti hai composto sulla tua vetrata", 4);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
