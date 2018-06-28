package project.ing.soft.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Round implements Serializable {
    private final int [] playerIndexes;
    private final int roundNumber;
    private final Game aGame;
    private int curr;


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


    public Round(Round other, Game aGame){
        this.aGame = aGame;
        this.curr  = other.curr;
        this.roundNumber = other.roundNumber;
        this.playerIndexes = Arrays.copyOf(other.playerIndexes,other.playerIndexes.length);
    }


    public boolean hasNext() {
        return curr +1 < playerIndexes.length;
    }

    public Player next() {

        if(!hasNext()) {
            throw  new NoSuchElementException();
        }else{
            curr++;
            return aGame.getPlayers().get(playerIndexes[curr]);
        }

    }

    public void repeatCurrentPlayer(){

        if(curr >= aGame.getPlayers().size())
            return;
        for (int i = playerIndexes.length-curr-1; curr<i; i--) playerIndexes[i] = playerIndexes[i - 1];

    }

    public List<Player> getRemaining(){
        return Arrays.stream(playerIndexes).filter(i-> i>= curr).mapToObj( aGame.getPlayers()::get).collect(Collectors.toList());
    }

    public Player getCurrent(){
        return aGame.getPlayers().get(playerIndexes[curr]);
    }

    public Round nextRound(){
        return new Round(roundNumber+1, this.aGame);

    }

}