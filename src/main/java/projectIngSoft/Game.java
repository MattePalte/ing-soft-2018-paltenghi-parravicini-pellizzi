package projectIngSoft;


import java.io.Serializable;
import java.util.*;


public class Game implements Serializable{

    private final int maxNumPlayer;
    private ArrayList<Player> players;


    /*
    @requires theNumOfPlayer > 0
    @ensures

        (* everything is initialized *)
    */
    public Game(int theMaxNumOfPlayer) {
        // set required number of players for this game
        maxNumPlayer = theMaxNumOfPlayer;
        // initialize emplty list of player
        players = new ArrayList<Player>();
    }

    public Game(Game aGame){
        maxNumPlayer = aGame.maxNumPlayer;
        players = new ArrayList<Player>(aGame.players);
    }

    public int getMaxNumPlayers() {
        return maxNumPlayer;
    }

    /*
        @ensures
            getNumberOfPlayers() == old(getNumberOfPlayers()) + 1  &&
            (* newPlayer has been added to the list of players *)
        */
    public void add(Player newPlayer) {
        if (players.size() < maxNumPlayer) {
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
        return players.size() <= maxNumPlayer;
    }

    public void leftShiftPlayers(){
        ArrayList<Player> shiftedList = new ArrayList<>();
        for(int i = 0; i < players.size(); i++){
            shiftedList.add(i, players.get((i + 1) % players.size()));
        }
        players = shiftedList;
    }

}