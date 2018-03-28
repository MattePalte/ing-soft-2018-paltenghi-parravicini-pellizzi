package projectIngSoft;

import java.util.ArrayList;

public class Game {

    private ArrayList<Dice> diceBag = new ArrayList<Dice>();

    public Game() {
        // populate Dice Bag
        for (Colour c : Colour.values()){
            for(int i = 1; i <= 6; i++){
                Dice newDice = new Dice(i, c);
                this.diceBag.add(newDice);
            }
        }
    }



}
