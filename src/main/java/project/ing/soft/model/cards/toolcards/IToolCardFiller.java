package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.UserInterruptActionException;

public interface IToolCardFiller {

    void fill(AlesatoreLaminaRame aToolcard)     throws InterruptedException, UserInterruptActionException;
    void fill(DiluentePastaSalda aToolcard)      throws InterruptedException, UserInterruptActionException;
    void fill(Lathekin aToolcard)                throws InterruptedException, UserInterruptActionException;
    void fill(Martelletto aToolcard)             throws InterruptedException, UserInterruptActionException;
    void fill(PennelloPastaSalda aToolcard)      throws InterruptedException, UserInterruptActionException;
    void fill(PennelloPerEglomise aToolcard)     throws InterruptedException, UserInterruptActionException;
    void fill(PinzaSgrossatrice aToolcard)       throws InterruptedException, UserInterruptActionException;
    void fill(RigaSughero aToolcard)             throws InterruptedException,  UserInterruptActionException;
    void fill(StripCutter aToolcard)  ;
    void fill(TaglierinaManuale aToolcard)       throws InterruptedException, UserInterruptActionException;
    void fill(TaglierinaCircolare aToolcard)     throws InterruptedException, UserInterruptActionException;
    void fill(TamponeDiamantato aToolcard)       throws InterruptedException, UserInterruptActionException;
    void fill(TenagliaRotelle aToolcard)         throws InterruptedException, UserInterruptActionException;


}
