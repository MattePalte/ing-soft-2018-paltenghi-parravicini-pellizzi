package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowPattern;

public class SfumatureDiverseColonna extends PublicObjective {

    public SfumatureDiverseColonna(){
        super("Sfumature Diverse - Colonna", "Hai formato colonne senza ripetere piu volte lo stesso grado di sfumatura", 4);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowPattern window) {
        return 0;
    }
}
