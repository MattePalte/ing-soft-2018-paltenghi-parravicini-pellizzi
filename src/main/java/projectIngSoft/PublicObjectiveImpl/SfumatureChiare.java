package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class SfumatureChiare extends PublicObjective {

    public SfumatureChiare(){
        super("Sfumature Chiare","Conta il numero di coppie di dadi (1,2) presenti sulla tua vetrata", 2);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
