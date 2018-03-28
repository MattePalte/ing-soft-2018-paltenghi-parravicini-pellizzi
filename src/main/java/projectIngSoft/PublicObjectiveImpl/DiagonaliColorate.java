package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowPattern;

public class DiagonaliColorate extends PublicObjective {

    public DiagonaliColorate(){
        super("Diagonali Colorate", "Conta il numero di dadi dello stesso colore posizionati diagonalmente l'uno rispetto all'altro", 1);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
