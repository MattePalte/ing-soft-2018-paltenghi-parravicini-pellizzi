package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowPattern;

public class SfumatureScure extends PublicObjective {

    public SfumatureScure(){
        super("Sfumature Scure", "Conta il numero di coppie di dadi (5,6) presenti sulla tua vetrata", 2);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
