package projectIngSoft.PrivateObjectiveImpl;

import projectIngSoft.PrivateObjective;
import projectIngSoft.WindowPattern;

public class SfumatureVerdi extends PrivateObjective {

    public SfumatureVerdi(){
        super("Sfumature Verdi", "Conta quanti dadi verdi hai posizionato nella tua vetrata", 1);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
