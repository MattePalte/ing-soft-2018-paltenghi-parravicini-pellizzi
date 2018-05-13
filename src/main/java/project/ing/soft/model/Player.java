package project.ing.soft.model;

import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.gamemanager.events.Event;
import project.ing.soft.exceptions.PatternConstraintViolatedException;
import project.ing.soft.exceptions.PositionOccupiedException;
import project.ing.soft.exceptions.RuleViolatedException;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;

import project.ing.soft.model.cards.WindowPatternCard;


import project.ing.soft.view.IView;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Player implements Serializable{
    private final String            name;

    private boolean                 isPatternFlipped;
    private WindowPatternCard       myWindowPatternCard;
    private List<WindowPatternCard> possiblePatternCards;

    private PrivateObjective        myPrivateObjective;
    private Die[][]                 placedDice;

    private transient IView         myView;
    private boolean                 hasPlacedADieInThisTurn;
    private boolean                 hasEverPlacedADie;




    //In order to let the player use patterns with non-predefined dimensions
    //the player placedDice matrix is created when patternCard is chosen.
    public Player(String name, IView aView) {
        this.name                    = name;
        this.isPatternFlipped        = false;
        this.myWindowPatternCard     = null;
        this.possiblePatternCards    = new ArrayList<>();
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
        this.possiblePatternCards    = new ArrayList<>(pToBeCopied.possiblePatternCards);
        this.myPrivateObjective      = pToBeCopied.myPrivateObjective;
        this.placedDice              = cloneArray(pToBeCopied.placedDice);
        this.myView                  = pToBeCopied.myView;
        this.hasPlacedADieInThisTurn = pToBeCopied.hasPlacedADieInThisTurn;
        this.hasEverPlacedADie       = pToBeCopied.hasEverPlacedADie;
    }

    public Player getMemento(){
        return new Player(this);
    }

    public void saveMemento(Player p){
        if(this.name.equals(p.name) ){
            this.isPatternFlipped        = p.isPatternFlipped;
            this.myWindowPatternCard     = p.myWindowPatternCard;
            this.possiblePatternCards    = new ArrayList<>(p.possiblePatternCards);
            this.myPrivateObjective      = p.myPrivateObjective;
            this.placedDice = cloneArray(p.placedDice);
            this.myView                  = p.myView;
            this.hasPlacedADieInThisTurn = p.hasPlacedADieInThisTurn;
            this.hasEverPlacedADie       = p.hasEverPlacedADie;
        }
    }

    //region setter
    public void givePossiblePatternCard(List<WindowPatternCard> givenPatternCards) {
        possiblePatternCards = new ArrayList<>(givenPatternCards);
    }

    public void setPatternCard(WindowPatternCard aWindowPatternCard) {
        this.myWindowPatternCard = (aWindowPatternCard);
        this.placedDice = new Die[getPattern().getHeight()][getPattern().getWidth()];
    }

    public void setPatternFlipped(boolean patternFlipped) {
        isPatternFlipped = patternFlipped;
    }

    public void setPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;
    }
//endregion

    //region getter

    //@assignable nothing
    public String getName() {
        return name;
    }
    //@assignable nothing
    public List<WindowPatternCard> getPossiblePatternCard(){
        return new ArrayList<>(possiblePatternCards);
    }
    //@assignable nothing
    public WindowPatternCard getPatternCard() {
        return myWindowPatternCard;
    }
    //@assignable nothing
    public WindowPattern getPattern(){
        if(myWindowPatternCard == null)
            return null;
        return !isPatternFlipped ? myWindowPatternCard.getFrontPattern() : myWindowPatternCard.getRearPattern();
    }
    //@assignable nothing
    public PrivateObjective getPrivateObjective() {
        return myPrivateObjective;
    }
    //@assignable nothing
    public Die[][] getPlacedDice(){
        return  cloneArray(placedDice);
    }
    //@assignable nothing
    public Die getPlacedDice(Coordinate c){
        Die aDie = placedDice[c.getRow()][ c.getCol()];
        if(aDie != null)
            return new Die(aDie);
        return null;
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

    public String getPlayerSecurityCode(){
        return UUID.randomUUID().toString();
    }
//endregion

    public int countPrivateObjectivesPoints(){
        return myPrivateObjective.countPoints(this);
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
    public void placeDie(Die aDie, int row, int col, boolean checkPresence) throws PositionOccupiedException, PatternConstraintViolatedException, RuleViolatedException {
        if(hasPlacedADieInThisTurn)
            throw new RuleViolatedException("Player can't place more than a die at turn.");

        if(placedDice[row][col]!= null) {
            throw new PositionOccupiedException("A die has already been placed here");
        }


        if(!hasEverPlacedADie && row !=0 && row != getPattern().getHeight()-1 && col != 0 && col != getPattern().getWidth()-1){
            throw new RuleViolatedException("Each player’s first die of the game must be placed on an edge or corner space");

        }

        checkPlaceDie                     (aDie, row, col, true, true, hasEverPlacedADie && checkPresence);
        placeDieWithoutConstraints        (aDie, row, col);


    }

    public void moveDice(List<Coordinate> start, List<Coordinate> end, boolean checkColour, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException, PositionOccupiedException {
        Player copyTest = this.getMemento();
        List<Die> queue = new ArrayList<>();


        for (int i = 0; i < Math.min(start.size(), end.size()); i++) {
            Die aDie = removeDie(start.get(i));
            if (aDie == null)
                throw new RuleViolatedException("There's no die to move at the specified cell");
            queue.add(aDie);
        }

        for (int i = 0; i < queue.size(); i++) {

            checkPlaceDie(queue.get(i),end.get(i), checkColour, checkValue, checkPresence);
            placedDice[end.get(i).getRow()][end.get(i).getCol()] = queue.get(i);
        }

        this.saveMemento(copyTest);
    }

    private Die removeDie(int row, int col){
        Die ret;
        ret = placedDice[row][col];
        placedDice[row][col] = null;
        return ret;
    }

    private Die removeDie(Coordinate c){
        return removeDie(c.getRow(), c.getCol());
    }

    public List<Coordinate> getCompatiblePositions(Die aDie){
        ArrayList<Coordinate> ret = new ArrayList<>();
        if(hasPlacedADieInThisTurn)
            return ret;

        for(int row = 0; row < placedDice.length; row++){
            for(int col = 0; col < placedDice[0].length; col++){
                try{
                    checkPlaceDie(aDie, row, col, true, true, hasEverPlacedADie);
                    ret.add(new Coordinate(row, col));
                } catch (Exception ignored) {
                    //because this method determines the possible die that can be placed into a
                    //location by the exception trowed
                }
            }
        }
        return ret;
    }

    private void checkPlaceDie(Die aDie, int row, int col, boolean checkColor, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException, PositionOccupiedException {
        //check pattern constraint in row,col
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

        if(placedDice[row][col]!= null) {
            throw new PositionOccupiedException("A die has already been placed here");
        }

    }

    private void checkPlaceDie(Die aDie, Coordinate c, boolean checkColor, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException, PositionOccupiedException {
        checkPlaceDie(aDie, c.getRow(), c.getCol(), checkColor, checkValue, checkPresence);
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

    public boolean isThereAnAdjacentDie(int row, int col)  {
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

    public int getEmptyCells(){
        int ret = 0;

        for(Die[] row : placedDice)
            for(Die die : row)
                if(die == null)
                    ret++;
        return ret;
    }
    public void endTurn() {
        hasPlacedADieInThisTurn = false;
    }

    //endregion


    public void update(Event event) throws IOException {
        myView.update(event);
    }

    //region object override

    @Override
    public boolean equals(Object o) {
        //if (this == o) return true
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return isPatternFlipped == player.isPatternFlipped &&
                hasPlacedADieInThisTurn == player.hasPlacedADieInThisTurn &&
                hasEverPlacedADie == player.hasEverPlacedADie &&
                Objects.equals(name, player.name) &&
                Objects.equals(myWindowPatternCard, player.myWindowPatternCard) &&
                Objects.equals(possiblePatternCards, player.possiblePatternCards) &&
                Objects.equals(myPrivateObjective, player.myPrivateObjective) &&
                Arrays.deepEquals(placedDice, player.placedDice) &&
                Objects.equals(myView, player.myView);
    }


    @Override
    public int hashCode() {

        int result = Objects.hash(name, isPatternFlipped, myWindowPatternCard, possiblePatternCards, myPrivateObjective, myView, hasPlacedADieInThisTurn, hasEverPlacedADie);
        result = 31 * result + Arrays.deepHashCode(placedDice);
        return result;
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
        if(getPattern() == null){
            aBuilder.append("Not already chosen a pattern card");
        }else {
            Constraint[][] constraintsMatrix = getPattern().getConstraintsMatrix();

            for (int r = 0; r < constraintsMatrix.length; r++) {
                for (int c = 0; c < constraintsMatrix[0].length; c++) {
                    //if die was placed
                    if (placedDice[r][c] != null) {
                        tmp = placedDice[r][c].toString();
                    } else {
                        //else take the constrain representation intself
                        tmp = constraintsMatrix[r][c].toString();
                    }
                    aBuilder.append(constraintsMatrix[r][c].getColour().colourBackground(tmp));
                }
                aBuilder.append("\n");
            }
        }
        aBuilder.append("---------------------\n");
        return aBuilder.toString();
    }

//endregion
}