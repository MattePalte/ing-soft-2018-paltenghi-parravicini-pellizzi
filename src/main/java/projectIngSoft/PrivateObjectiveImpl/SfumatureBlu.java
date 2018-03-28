package projectIngSoft.PrivateObjectiveImpl;

import projectIngSoft.PrivateObjective;
import projectIngSoft.WindowFrame;

public class SfumatureBlu extends PrivateObjective {

    public SfumatureBlu(){
        super("Sfumature Blu","Conta quanti dadi blu hai posizionato nella tua vetrata", 1);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
