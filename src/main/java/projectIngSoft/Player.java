package projectIngSoft;

import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPattern;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.View.IView;

public class Player {
    private final String     name;
    private WindowPatternCard myWindowPatternCard;
    private PrivateObjective myPrivateObjective;
    private Die[][]          placedDice;
    private boolean isPatternFlipped;
    private IView myView;

    //Constructor
    public Player(String name, IView aView) {
        this.name = new String(name);
        //altrimenti errore
        this.placedDice = new Die[1][1];
        this.myView = aView;
    }

    //---------------------- OBSERVERS -------------------------
    //@assignable nothing
    public String getName() {
        return new String(name);
    }

    //@assignable nothing
    public WindowPattern getVisiblePattern() {
        //TODO getVisiblePattern - class WindowPattern should be final?
        return getPattern();
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
        return  cloneArray(placedDice);
    }

    public void flip(){
        isPatternFlipped = !isPatternFlipped;
    }

    public WindowPattern getPattern(){
        return isPatternFlipped ? myWindowPatternCard.getFrontPattern() : myWindowPatternCard.getRearPattern();
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
        StringBuilder aBuilder =  new StringBuilder("Player{" +
                "\nname -> " + name +
                "\nmyWindowPatternCard ->\n" + myWindowPatternCard +
                "\nmyPrivateObjective ->\n" + myPrivateObjective.getTitle()+"\n");
        String tmp;
        Constraint[][] constraintsMatrix = getPattern().getConstraintsMatrix();

        for (int r = 0; r < constraintsMatrix.length ; r++) {
            for (int c = 0; c < constraintsMatrix[0].length; c++) {
                //if die was placed
                if(placedDice[r][c] != null){
                    tmp = placedDice[r][c].toString();
                }else{
                    //else take the constrain representation intself
                    tmp = constraintsMatrix[r][c].toString();
                }
                aBuilder.append(constraintsMatrix[r][c].getColour().ColourBackground(tmp));
            }
            aBuilder.append("\n");
        }
        return aBuilder.toString();
    }

    //---------------------- MODIFIERS -------------------------

    public void setPatternCard(WindowPatternCard aPatternCard) {
        this.myWindowPatternCard = aPatternCard;
        int width =  getPattern().getWidth();
        int height =  getPattern().getHeight();
        this.placedDice = new Die[height][width];
    }

    public void setPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;

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

    public void takeTurn() {
        myView.takeTurn();
    }

    //---------------------- NOT YET IMPLEMENTED METHODS - TO DISCUSS -------------------------

    public String getPlayerSecurityCode(){
        return new String("Codice");
    }

}