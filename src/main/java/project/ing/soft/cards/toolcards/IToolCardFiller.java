package project.ing.soft.cards.toolcards;

import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.view.LocalViewCli;

public interface IToolCardFiller {

    AlesatoreLaminaRame fill(AlesatoreLaminaRame aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    DiluentePastaSalda  fill(DiluentePastaSalda aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    Lathekin            fill(Lathekin aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    Martelletto         fill(Martelletto aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    PennelloPastaSalda  fill(PennelloPastaSalda aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    PennelloPerEglomise fill(PennelloPerEglomise aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    PinzaSgrossatrice   fill(PinzaSgrossatrice aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    RigaSughero         fill(RigaSughero aToolcard) throws UserInterruptActionException, InterruptedException, UserInterruptActionException;
    StripCutter         fill(StripCutter aToolcard)  ;
    TaglierinaManuale   fill(TaglierinaManuale aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    TaglierinaCircolare fill(TaglierinaCircolare aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    TamponeDiamantato   fill(TamponeDiamantato aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;
    TenagliaRotelle     fill(TenagliaRotelle aToolcard) throws InterruptedException, UserInterruptActionException, UserInterruptActionException;


}
