package projectIngSoft;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int numPlayers;
    private ArrayList<Die> diceBag = new ArrayList<Die>();
    private List<Die> draftsPool = new ArrayList<Die>();
    private RoundTracker rounds = RoundTracker.getInstance();

    public Game() {
        // populate Die Bag
        for (Colour c : Colour.values()){
            for(int i = 1; i <= 18; i++){
                Die newDie = new Die(c);
                this.diceBag.add(newDie);
            }
        }
    }



}
