package projectIngSoft;

import projectIngSoft.PrivateObjectiveImpl.*;
import projectIngSoft.PublicObjectiveImpl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Game {

    private final int numPlayers;
    private ArrayList<Player> players;
    private final ArrayList<Die> diceBag;
    private final ArrayList<Die> draftPool;
    private final RoundTracker rounds;
    private ArrayList<Card> privateObjectives;
    private ArrayList<Card> publicObjectives;
    private ArrayList<WindowPattern> windowPatterns;

    /*
    @requires theNumOfPlayer > 0
    @ensures
        (* everything is initialized *)
    */
    public Game(int theNumOfPlayer) {
        // set required number of players for this game
        numPlayers = theNumOfPlayer;
        // initialize emplty list of player
        players = new ArrayList<Player>();
        // create dies and populate Die Bag
        diceBag = new ArrayList<Die>();
        for (Colour c : Colour.values()) {
            for (int i = 1; i <= 18; i++) {
                Die newDie = new Die(c);
                this.diceBag.add(newDie);
            }
        }
        // initialize empty draft pool
        draftPool = new ArrayList<Die>();
        // initialize Round Tracker obj
        rounds = new RoundTracker();
        // initialize private Objective cards
        privateObjectives = new ArrayList<Card>();
        privateObjectives.add(new SfumatureBlu());
        privateObjectives.add(new SfumatureGialle());
        privateObjectives.add(new SfumatureRosse());
        privateObjectives.add(new SfumatureVerdi());
        privateObjectives.add(new SfumatureViola());
        // initialize public Objective cards
        publicObjectives = new ArrayList<Card>();
        publicObjectives.add(new ColoriDiversiColonna());
        publicObjectives.add(new ColoriDiversiRiga());
        publicObjectives.add(new DiagonaliColorate());
        publicObjectives.add(new SfumatureChiare());
        publicObjectives.add(new SfumatureDiverse());
        publicObjectives.add(new SfumatureDiverseColonna());
        publicObjectives.add(new SfumatureDiverseRiga());
        publicObjectives.add(new SfumatureMedie());
        publicObjectives.add(new SfumatureScure());
        publicObjectives.add(new VarietaColore());
        // initialize window pattern cards
        windowPatterns = new ArrayList<WindowPattern>();
        // Shuffle everything
        Collections.shuffle(diceBag);
        Collections.shuffle(publicObjectives);
        Collections.shuffle(privateObjectives);
        // remove cards and leave only 3 publicObjective card for the game
        publicObjectives = publicObjectives.stream().limit(3).collect(Collectors.toCollection(ArrayList::new));
    }

    /*
    @ensures
        getNumberOfPlayers() == old(getNumberOfPlayers()) + 1  &&
        (* newPlayer has been added to the list of players *)
    */
    public void add(Player newPlayer) {
        if (players.size() < numPlayers) {
            players.add(newPlayer);
            System.out.println("New player added: " + newPlayer.getName());
        }
    }

    /*
    @assignable nothing
    @ensures \result == (* number of players enrolled at the game now*)
    */
    public int getNumberOfPlayers(){
        return players.size();
    }

    public boolean isValid() {
        return players.size() == numPlayers;
    }

    public void setupPhase(){
        for (Player p : players) {
            PrivateObjective randomPrivateObjective = (PrivateObjective) privateObjectives.remove(0);
            p.setMyPrivateObjective(randomPrivateObjective);

            System.out.println("To " + p.getName() + " has been given : " + p.getMyPrivateObjective());

        }

    }


}
