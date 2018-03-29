package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class DiagonaliColorate extends PublicObjective {

    public DiagonaliColorate(){
        super("Diagonali Colorate", "Conta il numero di dadi dello stesso colore posizionati diagonalmente l'uno rispetto all'altro", 1);
    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
