package project.ing.soft.model;


import project.ing.soft.view.IView;

import java.io.Serializable;
import java.util.*;


/**
 * This class contains a game basic information about the players who joined it
 */
public class Game implements Serializable{

    private final int maxNumPlayer;
    private ArrayList<Player> players;


    /**
     * Game default constructor
     * @param theMaxNumOfPlayer the number of the maximum amount of players allowed in the game
     */
    public Game(int theMaxNumOfPlayer) {
        // set required number of players for this game
        maxNumPlayer = theMaxNumOfPlayer;
        // initialize empty list of player
        players = new ArrayList<>();
    }

    /**
     * Game producer. It creates a copy of the Game passed as a parameter
     * @param aGame the Game to be copied
     */
    public Game(Game aGame){
        this.maxNumPlayer = aGame.maxNumPlayer;
        this.players = new ArrayList<>();
        for(Player p : aGame.players){
            this.players.add(new Player(p));
        }
    }

    /**
     *
     * @return the maximum number of players allowed in the game
     */
    public int getMaxNumPlayers() {
        return maxNumPlayer;
    }


    /**
     * This method adds a player in the game, if there is still space
     * @param newPlayer the player to be added
     */
    public void add(Player newPlayer) {
        if (players.size() < maxNumPlayer) {
            players.add(newPlayer);
        }
    }

    /**
     * This method is used to reconnect a player who disconnected from the game and add its new
     * representation to the players' list
     * @param playerName the name of the player who asked reconnection
     * @param view player's new view reference
     */
    public void reconnect(String playerName, IView view){
        Player playerInfoBackup = players.stream().filter(p -> p.getName().equals(playerName)).findFirst().orElse(null);
        if(playerInfoBackup == null)
            return;
        int indexBackup = players.indexOf(playerInfoBackup);
        players.remove(playerInfoBackup);
        players.add(indexBackup, new Player(playerInfoBackup, view));
    }


    /**
     *
     * @return the number of players actually joined in the game
     */
    public int getNumberOfPlayers(){
        return players.size();
    }

    /**
     *
     * @return a copy of the list of players who already joined the game
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }


    /**
     *
     * @return a boolean flag which indicates if the game is valid or not. The game is considered valid if
     * the number of players joined into the game is less or at least equal to the maximum amount of players
     * allowed
     */
    public boolean isValid() {
        return players.size() <= maxNumPlayer;
    }

    /**
     * Method used to execute a circular left shift of the players list. It's useful to create the list
     * of the turns during the game
     */
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