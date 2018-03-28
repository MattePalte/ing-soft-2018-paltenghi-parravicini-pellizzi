package projectIngSoft.PrivateObjectiveImpl;

import projectIngSoft.PrivateObjective;
import projectIngSoft.WindowPattern;

public class SfumatureGialle extends PrivateObjective {

    public SfumatureGialle(){
        super("Sfumature Gialle", "Conta quanti dadi gialli hai posizionato nella tua vetrata", 1);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
