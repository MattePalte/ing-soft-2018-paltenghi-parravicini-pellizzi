package projectIngSoft;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final int numPlayers;
    private final ArrayList<Die> diceBag;
    private final ArrayList<Die> draftPool;
    private final RoundTracker rounds;
    private final ArrayList<Card> publicObjectives;

    public Game(int players) {
        numPlayers = players;
        diceBag = new ArrayList<Die>();
        draftPool = new ArrayList<Die>();
        rounds = new RoundTracker();
        publicObjectives = new ArrayList<Card>();
        // populate Die Bag
        for (Colour c : Colour.values()){
            for(int i = 1; i <= 18; i++){
                Die newDie = new Die(c);
                this.diceBag.add(newDie);
            }
        }
    }



}
