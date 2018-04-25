package projectIngSoft;

import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;

import projectIngSoft.Cards.WindowPattern;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;

import projectIngSoft.View.IView;
import projectIngSoft.events.Event;
import projectIngSoft.exceptions.PatternConstraintViolatedException;
import projectIngSoft.exceptions.PositionOccupiedException;
import projectIngSoft.exceptions.RuleViolatedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {
    private final String            name;

    private boolean                 isPatternFlipped;
    private WindowPatternCard       myWindowPatternCard;
    private List<WindowPatternCard> possiblePatternCards;

    private PrivateObjective        myPrivateObjective;
    private Die[][]                 placedDice;

    private IView                   myView;
    private boolean                 hasPlacedADieInThisTurn;
    private boolean                 hasEverPlacedADie;


    //In order to let the player use patterns with non-predefined dimensions
    //the player placedDice matrix is created when patternCard is chosen.
    public Player(String name, IView aView) {
        this.name                    = name;
        this.isPatternFlipped        = false;
        this.myWindowPatternCard     = null;
        this.possiblePatternCards    = null;
        this.myPrivateObjective      = null;
        this.placedDice              = new Die[1][1];
        this.myView                  = aView;
        this.hasPlacedADieInThisTurn = false;
        this.hasEverPlacedADie       = false;
    }

    // the only field that is actually copied is the placedDie matrix.
    // that is due to the fact that other fields are immutable objects
    public Player(Player pToBeCopied) {
        this.name                    = pToBeCopied.name;
        this.isPatternFlipped        = pToBeCopied.isPatternFlipped;
        this.myWindowPatternCard     = pToBeCopied.myWindowPatternCard;
        this.possiblePatternCards    = possiblePatternCards == null ? null : new ArrayList<>(pToBeCopied.possiblePatternCards);
        this.myPrivateObjective      = pToBeCopied.myPrivateObjective;
        this.placedDice              = cloneArray(pToBeCopied.placedDice);
        this.myView                  = pToBeCopied.myView;
        this.hasPlacedADieInThisTurn = pToBeCopied.hasPlacedADieInThisTurn;
        this.hasEverPlacedADie       = pToBeCopied.hasEverPlacedADie;
    }

    public String getName() {
        return name;
    }

    public void setPatternCard(WindowPatternCard aWindowPatternCard) {
        this.myWindowPatternCard = (aWindowPatternCard);
        this.placedDice = new Die[getPattern().getHeight()][getPattern().getWidth()];
    }

    public void setPatternFlipped(boolean patternFlipped) {

        isPatternFlipped = patternFlipped;
    }

    public void givePossiblePatternCard(List<WindowPatternCard> givenPatternCards) {
        possiblePatternCards = new ArrayList<>(givenPatternCards);
    }

    //@assignable nothing
    public List<WindowPatternCard> getPossiblePatternCard(){

        return new ArrayList<>(possiblePatternCards);
    }


    //@assignable nothing
    public WindowPatternCard getPatternCard() {

        return myWindowPatternCard;
    }

    public WindowPattern getPattern(){
        return !isPatternFlipped ? myWindowPatternCard.getFrontPattern() : myWindowPatternCard.getRearPattern();
    }

    //@assignable nothing
    public PrivateObjective getPrivateObjective() {
        return myPrivateObjective;
    }

    public int countPrivateObjectivesPoints(){
        return myPrivateObjective.countPoints(this);
    }

    //@assignable nothing
    public Die[][] getPlacedDice(){

        return  cloneArray(placedDice);
    }

    /**
     * Clones the provided array
     *
     * @param matrixOfDice a matrix to be deepCloned
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

    public boolean getHasPlacedADieInThisTurn(){
        return hasPlacedADieInThisTurn;
    }

    public void setPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;

    }

    //region die placement

    private void placeDieWithoutConstraints(Die aDie, int row, int col){
        placedDice[row][col] =    new Die(aDie);
        hasPlacedADieInThisTurn = true;
        hasEverPlacedADie =       true;

    }

    // Exceptions thrown if:
    //      a die is placed in an occupied position -> PositionOccupiedException
    //      a die placed doesn't match rules -> RuleViolatedExceptions
    //      a pattern constraint is violated -> PatternConstraintViolatedException
    public void placeDie(Die aDie, int row, int col) throws PositionOccupiedException, PatternConstraintViolatedException, RuleViolatedException {
        if(hasPlacedADieInThisTurn)
            throw new RuleViolatedException("Player can't place more than a die at turn.");

        if(placedDice[row][col]!= null) {
            throw new PositionOccupiedException("A die has already been placed here");
        }


        if(!hasEverPlacedADie && row !=0 && row != getPattern().getHeight()-1 && col != 0 && col != getPattern().getWidth()-1){
            throw new RuleViolatedException("Each player’s first die of the game must be placed on an edge or corner space");

        }


        checkPlaceDie                     (aDie, row, col, true, true, hasEverPlacedADie);
        placeDieWithoutConstraints        (aDie, row, col);


    }

    public void moveDie(Coordinate start, Coordinate end, boolean checkColour, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException {
        if (placedDice[end.getRow()][end.getCol()] != null)
            throw new RuleViolatedException("The destination cell is already occupied");
        if (placedDice[start.getRow()][start.getCol()] == null)
            throw new RuleViolatedException("There's no die to move at the specified cell");
        Die dieToMove = placedDice[start.getRow()][start.getCol()];


        checkPlaceDie(dieToMove, end.getRow(), end.getCol(), checkColour, checkValue, checkPresence);
    }

    private void checkPlaceDie(Die aDie, int row, int col, boolean checkColor, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException {
        //check pattern contraint in row,col
        Constraint actualConstraint = getPattern().getConstraintsMatrix()[row][col];

        if(checkColor && !actualConstraint.compatibleWithColour(aDie))
            throw new PatternConstraintViolatedException("Ehi, you cheater! You are violating a Color constraint on your pattern! Try again, and play fairly!");
        if(checkValue && !actualConstraint.compatibleWithValue(aDie) )
            throw new PatternConstraintViolatedException("Ehi, you cheater! You are violating a Value constraint on your pattern! Try again, and play fairly!");


        for(Die aOrthogonalDie : getOrthogonalAdjacents(getPlacedDice(),row,col)){


            if(checkColor && aOrthogonalDie.getColour().equals(aDie.getColour()))
                throw new RuleViolatedException("Ehi! You are trying to place a die with the same colour. You can't do whatever you want! You must follow the rules");
            if(checkValue && aOrthogonalDie.getValue() == aDie.getValue())
                throw new RuleViolatedException("Ehi! You are trying to place the same value than an adjacent die. You can't do whatever you want! You must follow the rules");
        }

        if(checkPresence && !isThereAnAdjacentDie(row, col)){
            throw new RuleViolatedException("Die must be placed near an already placed die!");
        }
    }

    private ArrayList<Die> getOrthogonalAdjacents(Die[][] placedDice, int row, int col){
        ArrayList<Die> ret = new ArrayList<>();

        if(col + 1 < placedDice[row].length && placedDice[row][col+1] != null)
            ret.add(placedDice[row][col+1]);
        if(col > 0 && placedDice[row][col-1] != null)
            ret.add(placedDice[row][col-1]);
        if(row + 1 < placedDice.length && placedDice[row+1][col] != null)
            ret.add(placedDice[row+1][col]);
        if(row > 0 && placedDice[row-1][col] != null)
            ret.add(placedDice[row-1][col]);

        return ret;
    }

    private boolean isThereAnAdjacentDie(int row, int col)  {
        for (int deltaRow = -1; deltaRow <= 1 ; deltaRow++) {
            for (int deltaCol = -1; deltaCol <= 1; deltaCol++) {

                if(     !(deltaCol == 0 && deltaRow == 0) &&
                        row+deltaRow >=0 && row+deltaRow < getPattern().getHeight() &&
                        col+deltaCol >= 0 && col+deltaCol < getPattern().getWidth() &&
                        placedDice[row+deltaRow][col+deltaCol] != null)
                   return true;
            }
        }
        return false;
    }

    public void resetDieFlag() {
        hasPlacedADieInThisTurn = false;
    }

    //endregion


    public void update(Event event) {
        myView.update( event);
    }

    public void giveControllerToTheView(IController aController) {

        myView.attachController(aController);
    }

    public String getPlayerSecurityCode(){
        //TODO: build a code that identifies my view.
        return new String("Codice");
    }

    /**
     *
     * @return a string representation of player. The WindowPatternCard is not printed, because it's already integrated in the background
     */
    @Override
    public String toString() {
        StringBuilder aBuilder =  new StringBuilder();
        aBuilder.append("---------------------\n");
        aBuilder.append(getName());
        aBuilder.append("'s situation ...\n");
        aBuilder.append("PrivObj : ");
        aBuilder.append(myPrivateObjective.getTitle());
        aBuilder.append("\n");

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
        aBuilder.append("---------------------\n");
        return aBuilder.toString();
    }

}