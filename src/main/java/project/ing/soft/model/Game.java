package project.ing.soft.model;


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
        // initialize empty list of player
        players = new ArrayList<>();
    }

    public Game(Game aGame){
        this.maxNumPlayer = aGame.maxNumPlayer;
        this.players = new ArrayList<>();
        for(Player p : aGame.players){
            this.players.add(new Player(p));
        }
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
            newPlayer.setConnected(true);
        }
    }

    public void add(Player newPlayer, int index){
        if(players.size() < maxNumPlayer){
            players.add(index, newPlayer);
        }
    }

    public void remove(String nickname){
        Optional<Player> toRemove = players.stream().filter(p -> p.getName().equals(nickname)).findFirst();
        toRemove.ifPresent(p -> players.remove(p));
    }

    /*
    @assignable nothing
    @ensures \result == (* number of players enrolled at the game now*)
    */
    public int getNumberOfPlayers(){
        return players.size();
    }

    public List<Player> getPlayers() {
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

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return maxNumPlayer == game.maxNumPlayer &&
                players.equals(game.players);
    }

    @Override
    public int hashCode() {

        return Objects.hash(maxNumPlayer, players);
    }
}