package projectIngSoft;


import java.io.FileNotFoundException;
import java.util.*;


public class Game {

    private final int numPlayers;
    private ArrayList<Player> players;


    /*
    @requires theNumOfPlayer > 0
    @ensures

        (* everything is initialized *)
    */
    public Game(int theNumOfPlayer) throws FileNotFoundException, Colour.ColorNotFoundException {
        // set required number of players for this game
        numPlayers = theNumOfPlayer;
        // initialize emplty list of player
        players = new ArrayList<Player>();
    }

    public Game(Game aGame){
        numPlayers = aGame.numPlayers;
        players = new ArrayList<Player>(aGame.players);
    }

    /*
    @ensures
        getNumberOfPlayers() == old(getNumberOfPlayers()) + 1  &&
        (* newPlayer has been added to the list of players *)
    */
    public void add(Player newPlayer) {
        if (players.size() < numPlayers) {
            players.add(newPlayer);
            System.out.println("New player added: " + newPlayer.getName() + "\n");
        }
    }

    /*
    @assignable nothing
    @ensures \result == (* number of players enrolled at the game now*)
    */
    public int getNumberOfPlayers(){
        return players.size();
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(players);
    }


    public boolean isValid() {
        return players.size() == numPlayers;
    }


}