package projectIngSoft.PrivateObjectiveImpl;

import projectIngSoft.PrivateObjective;
import projectIngSoft.WindowPattern;

public class SfumatureRosse extends PrivateObjective {

    public SfumatureRosse(){
        super("Sfumature Rosse", "Conta quanti dadi rossi hai posizionato nella tua vetrata", 1);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
