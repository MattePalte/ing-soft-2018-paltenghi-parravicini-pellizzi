package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

public class SfumatureDiverse extends PublicObjective {

    public SfumatureDiverse(){
        super("Sfumature Diverse", "Conta quanti set di sfumature (1,2,3,4,5,6) sei riuscito a comporre sulla tua vetrata", 5);
    }

    public void countPoints() {

    }

    public int checkCondition(WindowFrame window) {
        return 0;
    }
}
