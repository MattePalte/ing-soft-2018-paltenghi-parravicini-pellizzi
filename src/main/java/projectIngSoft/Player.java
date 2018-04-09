package projectIngSoft;

import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPattern;
import projectIngSoft.Cards.WindowPatternCard;

import java.net.Socket;
import java.util.Arrays;

public class Player {
    private final String     name;
    private WindowPatternCard myWindowPatternCard;
    private PrivateObjective myPrivateObjective;
    private Die[][]          placedDice;

    //Constructor
    public Player(String name) {
        this.name = new String(name);
    }

    //---------------------- OBSERVERS -------------------------
    //@assignable nothing
    public String getName() {
        return new String(name);
    }

    //@assignable nothing
    public WindowPattern getVisiblePattern() {
        //TODO getVisiblePattern - class WindowPattern should be final?
        return myWindowPatternCard.getCurrentPattern();
    }

    //@assignable nothing
    public WindowPatternCard getPatternCard() {
        //TODO getPatternCard - should pass a clone?
        return myWindowPatternCard;
    }

    //@assignable nothing
    public PrivateObjective getPrivateObjective() {
        //TODO getPrivateObjective - private objective has no modifier, should we clone it before return?
        return myPrivateObjective;
    }

    //@assignable nothing
    public Die[][] getPlacedDice(){
        return cloneArray(placedDice);
    }

    /**
     * Clones the provided array
     *
     * @param matrixOfDice
     * @return a new clone of the provided matrix of die
     */
    private Die[][] cloneArray(Die[][] matrixOfDice) {
        int length = matrixOfDice.length;
        Die[][] target = new Die[length][matrixOfDice[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(matrixOfDice[i], 0, target[i], 0, matrixOfDice[i].length);
        }
        return target;
    }

    @Override
    public String toString() {
        return "Player{" +
                "\nname -> " + name +
                "\nmyWindowPatternCard ->\n" + myWindowPatternCard +
                "\nmyPrivateObjective ->\n" + myPrivateObjective.getTitle() +
                "\nplacedDice ->\n" + Arrays.deepToString(placedDice) +
                "\n}";
    }

    //---------------------- MODIFIERS -------------------------

    public void setPatternCard(WindowPatternCard aPatternCard) {
        this.myWindowPatternCard = aPatternCard;
        int width =  aPatternCard.getCurrentPattern().getWidth();
        int height =  aPatternCard.getCurrentPattern().getHeight();
        this.placedDice = new Die[height][width];
    }

    public void setPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;

    }

    public void flipCard(){
        this.myWindowPatternCard.flip();
    }

    public boolean placeDie(Die aDie, int row, int col) {
        //TODO placeDie - tenere conto se la posizione è già occupata, decidere politica di errore bolean vs Exception (chi le gestirà?)
        try {
            placedDice[row][col] = (Die) aDie.clone();
            return true;
        } catch (IndexOutOfBoundsException | CloneNotSupportedException e) {
            return false;
        }
    }

    public boolean playToolCard(ToolCard aToolCard){
        return true;
    }

    //---------------------- NOT YET IMPLEMENTED METHODS - TO DISCUSS -------------------------

    public int getFavoursLeft(){
        return 0;
    }

    public Socket getPlayerSocket(){
        return null;
    }

    public String getPlayerSecurityCode(){
        return new String("Codice");
    }


}