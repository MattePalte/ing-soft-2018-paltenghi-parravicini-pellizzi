package projectIngSoft.PrivateObjectiveImpl;

import projectIngSoft.PrivateObjective;
import projectIngSoft.WindowPattern;

public class SfumatureViola extends PrivateObjective {

    public SfumatureViola(){
        super("Sfumature Viola", "Conta quanti dadi viola hai posizionato nella tua vetrata", 1);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
