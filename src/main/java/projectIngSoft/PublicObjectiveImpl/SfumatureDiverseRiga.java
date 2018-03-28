package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class SfumatureDiverseRiga extends PublicObjective {

    public SfumatureDiverseRiga(){
        super("Sfumature Diverse - Riga", "Hai formato righe senza ripetere piu volte lo stesso grado di sfumatura", 5);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
