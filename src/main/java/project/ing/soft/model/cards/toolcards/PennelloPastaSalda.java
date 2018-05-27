package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Colour;
import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.gamemanager.IGameManager;
import project.ing.soft.model.Player;
import project.ing.soft.exceptions.MalformedToolCardException;

public class PennelloPastaSalda extends ToolCardStateful {

    public PennelloPastaSalda() {
        super("Pennello per pasta salda", "Dopo aver scelto un dado, tira nuovamente quel dado. \n" +
                "Se non puoi piazzarlo, riponilo nella Riserva",
                "toolcard/30%/toolcards-7.png", Colour.VIOLET);
        super.setState(new PennelloPastaSaldaFirstPart());
    }

    public PennelloPastaSalda(PennelloPastaSalda from){
        super(from);
    }

    public ToolCardStateful copy(){
        return new PennelloPastaSalda(this);
    }

}
