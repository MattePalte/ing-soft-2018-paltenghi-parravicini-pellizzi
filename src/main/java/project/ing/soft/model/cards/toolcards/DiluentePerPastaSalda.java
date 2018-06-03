package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.Colour;


public class DiluentePerPastaSalda extends ToolCardStateful {


    public DiluentePerPastaSalda() {
        super("Diluente per pasta salda", "Dopo aver scelto un dado, riponilo nel \n" +
                        "sacchetto, poi pescane uno dal Sacchetto. Scegli il valore del nuovo dado e \n" +
                        "piazzalo, rispettando tutte le restrizioni di piazzamento",
                "toolcard/30%/toolcards-12.png",  Colour.VIOLET);
        super.setState(new DiluentePerPastaSaldaFirstPart());
    }

    public DiluentePerPastaSalda(DiluentePerPastaSalda from) {
        super(from);
    }

    @Override
    public ToolCardStateful copy() {
        return new DiluentePerPastaSalda(this);
    }
}

