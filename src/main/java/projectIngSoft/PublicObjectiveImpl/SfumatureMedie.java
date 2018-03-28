package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class SfumatureMedie extends PublicObjective{

    public SfumatureMedie(){
        super("Sfumature Medie", "Conta il numero di coppie di dadi (3,4) presenti sulla tua vetrata", 2);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
