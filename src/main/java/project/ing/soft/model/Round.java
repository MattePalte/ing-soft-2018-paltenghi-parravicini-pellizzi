package project.ing.soft.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * This class manages the player's appearance during the round
 */
public class Round implements Serializable {
    private final int [] playerIndexes;
    private final int roundNumber;
    private final Game aGame;
    private int curr;

    /**
     * A round is built from
     * @param roundNumber that identifies the round
     * @param aGame that carries the players
     */
    public Round(int roundNumber, Game aGame){
        this.aGame = aGame;
        this.curr = 0;
        this.roundNumber = roundNumber;
        playerIndexes = new int[aGame.getPlayers().size()*2];
        for (int i = 0; i < aGame.getPlayers().size(); i++) {
            playerIndexes[i] = (i+ roundNumber) % aGame.getPlayers().size();
            playerIndexes[aGame.getPlayers().size()*2-i-1] = (i+ roundNumber) % aGame.getPlayers().size() ;
        }
    }

    /**
     * Since the round object it's strictly connected with
     * the game from which the player's information can be gathered
     * when a round is copied the reference to it's game has to change
     * @param other round to copy
     * @param aGame that would be connected to the copy of the round
     */
    public Round(Round other, Game aGame){
        this.aGame = aGame;
        this.curr  = other.curr;
        this.roundNumber = other.roundNumber;
        this.playerIndexes = Arrays.copyOf(other.playerIndexes,other.playerIndexes.length);
    }

    private int firstNextConnectedPlayer() {
        int i = curr+1;
        while(i < playerIndexes.length && !aGame.getPlayers().get(playerIndexes[i]).isConnected() ) {
            i++;
        }
        return i;

    }

    public boolean hasNext() {

        return firstNextConnectedPlayer() < playerIndexes.length;
    }

    public Player next() {
        curr = firstNextConnectedPlayer();
        if(curr >= playerIndexes.length) {
            throw  new NoSuchElementException();
        }else{
            return aGame.getPlayers().get(playerIndexes[curr]);
        }

    }

    public void repeatCurrentPlayer(){
        int i = playerIndexes.length-1;
        while(i > curr && playerIndexes[i] != playerIndexes[curr]){
            //find the first right occurrence of the same player
            i--;
        }
        //if an occurrence got found
        if( playerIndexes[i] == playerIndexes[curr]) {
            //the sub array (curr,..,i] gets right shifted
            while (i > curr) {
                playerIndexes[i] = playerIndexes[i - 1];
                i--;
            }
        }
    }

    /**
     * @return the turn list of players that will follow the game
     */
    public List<Player> getRemaining(){
        return Arrays.stream(playerIndexes).filter(i-> i>= curr).mapToObj( aGame.getPlayers()::get).collect(Collectors.toList());
    }

    /**
     * @return the player that has the right to play this turn
     */
    public Player getCurrent(){
        return aGame.getPlayers().get(playerIndexes[curr]);
    }

    /**
     * Used to get through the next round
     * @return the round that follows
     */
    public Round nextRound(){
        return new Round(roundNumber+1, this.aGame);

    }

}