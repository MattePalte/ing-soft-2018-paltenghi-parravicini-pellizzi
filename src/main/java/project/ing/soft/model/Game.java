package project.ing.soft.model;


import project.ing.soft.model.cards.objectives.privates.RearPrivateObjective;
import project.ing.soft.view.IView;

import java.io.Serializable;
import java.util.*;


/**
 * This class contains a game basic information about the players who joined it
 */
public class Game implements Serializable, Iterable<Player>{

    private final int maxNumPlayer;
    private final Map<String,Integer> nameToIndex;
    private final List<Player> players;



    /**
     * Game default constructor
     * @param theMaxNumOfPlayer the number of the maximum amount of players allowed in the game
     */
    public Game(int theMaxNumOfPlayer) {
        // set required number of players for this game
        maxNumPlayer = theMaxNumOfPlayer;
        // initialize empty list of player
        players = new ArrayList<>();
        nameToIndex = new HashMap<>();
    }

    /**
     * Game producer. It creates a copy of the Game passed as a parameter
     * @param aGame the Game to be copied
     */
    public Game(Game aGame){
        this.maxNumPlayer = aGame.maxNumPlayer;
        this.nameToIndex = new HashMap<>();
        this.players = new ArrayList<>();
        for (int i = 0; i < aGame.players.size(); i++) {
            Player p = new Player(aGame.players.get(i));
            this.players.add(p);
            this.nameToIndex.put(aGame.players.get(i).getName(), i );
        }

    }

    /**
     * Game producer. It creates a copy of the Game passed as a parameter that does not
     * reveal private objectives of the players
     * @param aGame the Game to be copied
     * @param recipient that will receive the copy of the game
     */
    public Game(Game aGame, Player recipient){
        this.maxNumPlayer = aGame.maxNumPlayer;
        this.nameToIndex = new HashMap<>();
        this.players = new ArrayList<>();
        for (int i = 0; i < aGame.players.size(); i++) {
            Player p = new Player(aGame.players.get(i));
            if(!p.getName().equals(recipient.getName()))
                p.setPrivateObjective(new RearPrivateObjective());
            this.players.add(p);

            this.nameToIndex.put(aGame.players.get(i).getName(), i );
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
            players.add( newPlayer);
            nameToIndex.put(newPlayer.getName(),players.indexOf(newPlayer));
        }
    }

    /**
     * Remove a player
     * @param aPlayerName that identifies the player
     * @return the player removed
     */
    public Object remove(String aPlayerName){
        int i = nameToIndex.remove(aPlayerName);
        return players.remove(i);
    }

    /**
     * Retrieves the player based on the name
     * @param nickname of the player
     * @return a player that has @param nickname
     */
    public Player getPlayerFromName(String nickname){
        Integer i = nameToIndex.get(nickname);
        return i == null ? null : players.get(i);
    }

    /**
     * This method is used to reconnect a player who disconnected from the game and add its new
     * representation to the players' list
     * @param playerName the name of the player who asked reconnection
     * @param view player's new view reference
     */
    public void reconnect(String playerName, IView view){
        Player p = getPlayerFromName(playerName);
        if(p == null)
            return;
        p.reconnectView(view);

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

    /**
     * Returns an iterator over elements of type {@code T}.
     * @return an Iterator.
     */
    @Override
    public Iterator<Player>  iterator() {
        return players.iterator();
    }


}