package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.Colour;
import projectIngSoft.Die;
import projectIngSoft.PublicObjective;
import projectIngSoft.WindowFrame;

import java.util.*;

public class VarietaColore extends PublicObjective {

    public VarietaColore(){
        super("Varieta Colore", "Conta quanti set di dadi con 5 colori differenti hai composto sulla tua vetrata", 4);
    }

    public int checkCondition(WindowFrame window) {
        Die[][] placedDice = window.getPlacedDice();
        Map<Colour, Integer> colorQty = new HashMap<>();

        //Initializing local variable "values" to <[Colour],0> for each valid Colour
        Colour.validColours().forEach( colour -> colorQty.put(colour,0));

        for(Die[] row : placedDice)
            for(Die d : row){
                colorQty.put(d.getColour(), colorQty.get(d.getColour()) + 1);
            }
        ArrayList<Integer> values = new ArrayList<Integer>(colorQty.values());
        values.sort((fst,snd) -> {
            if(fst <= snd)
                return fst;
            else
                return snd;

        });
        return values.get(0);
    }
}