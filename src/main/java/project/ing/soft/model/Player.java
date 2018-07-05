package project.ing.soft.model;

import project.ing.soft.model.cards.Card;
import project.ing.soft.model.cards.WindowPattern;
import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.exceptions.PatternConstraintViolatedException;
import project.ing.soft.exceptions.PositionOccupiedException;
import project.ing.soft.exceptions.RuleViolatedException;
import project.ing.soft.model.cards.Constraint;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;

import project.ing.soft.model.cards.WindowPatternCard;


import project.ing.soft.view.IView;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * The Player class is a representation of a player in the game. It contains all the
 * information about the player itself
 */
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
    private boolean                 isConnected;



    //In order to let the player use patterns with non-predefined dimensions
    //the player placedDice matrix is created when patternCard is chosen.

    /**
     * Default Player constructor. It saves player's name and a reference to its view.
     * In order to let the player use patterns with non-predefined dimensions
     * the player "placedDice" matrix is created when the patternCard is chosen.
     * @param name of the player
     * @param aView reference to the player's view
     */
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
        this.isConnected             = true;
    }

    // the only field that is actually copied is the placedDie matrix.
    // that is due to the fact that other fields are immutable objects

    /**
     * Player productor. It takes a Player instance as a parameter and copy its information.
     * The only field that is actually copied is the "placedDie" matrix.
     * This is due to the fact that other fields are immutable objects
     * @param pToBeCopied player whose information will be copied
     */
    public Player(Player pToBeCopied) {
        this.name                    = pToBeCopied.name;
        this.isPatternFlipped        = pToBeCopied.isPatternFlipped;
        this.isConnected             = pToBeCopied.isConnected;
        this.myWindowPatternCard     = pToBeCopied.myWindowPatternCard;
        this.possiblePatternCards    = new ArrayList<>(pToBeCopied.possiblePatternCards);
        this.myPrivateObjective      = pToBeCopied.myPrivateObjective;
        this.placedDice              = cloneArray(pToBeCopied.placedDice);
        this.myView                  = pToBeCopied.myView;
        this.hasPlacedADieInThisTurn = pToBeCopied.hasPlacedADieInThisTurn;
        this.hasEverPlacedADie       = pToBeCopied.hasEverPlacedADie;
        this.isConnected = pToBeCopied.isConnected;
    }

    /**
     * Player productor. This specific method is used only if the player asks for a reconnection
     * before the match started. In that case it's necessary to copy all player information,
     * but the view reference must be changed
     * @param pToBeCopied player whose information will be copied
     * @param view new reference to the player's view
     */
    public Player(Player pToBeCopied, IView view){
        this(pToBeCopied);
        this.myView = view;
    }


    //region memento

    /**
     * Method which creates a memento of the player by copying its information
     * @return a player memento
     */
    public Player getMemento(){
        return new Player(this);
    }

    /**
     * Method which saves information contained in a player memento to the player's
     * current state
     * @param p player whose information are saved in the player's current state
     */
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
            this.isConnected             = p.isConnected;
        }
    }
    //endregion


    //region setter

    /**
     * Setter of the pattern card needed to let player choose its pattern
     * @param givenPatternCards list of 2 pattern cards
     */
    public void givePossiblePatternCard(List<WindowPatternCard> givenPatternCards) {
        possiblePatternCards = new ArrayList<>(givenPatternCards);
    }

    /**
     * Setter of the pattern card. Used to save player's choice about the pattern card
     * @param aWindowPatternCard the pattern card chosen by the player
     */
    public void setPatternCard(WindowPatternCard aWindowPatternCard) {
        this.myWindowPatternCard = (aWindowPatternCard);
        this.placedDice = new Die[getPattern().getHeight()][getPattern().getWidth()];
    }

    /**
     * Setter of a boolean flag which indicates if the player chose the front or the rear
     * frontSide of the pattern card
     * @param patternFlipped a flag to indicate which frontSide of the pattern the player chose
     */
    public void setPatternFlipped(boolean patternFlipped) {
        isPatternFlipped = patternFlipped;
    }

    /**
     * Setter of the player's private objective
     * @param myPrivateObjective the private objective given to the player
     */
    public void setPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;
    }

    //endregion

    //region getter

    /**
     * @return a boolean flag which indicates if the player is still connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Getter of the player's name
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Getter of the list of possible pattern cards the player can choose
     * @return a copy of the list of PossiblePatternCards
     */
    public List<WindowPatternCard> getPossiblePatternCard(){
        return new ArrayList<>(possiblePatternCards);
    }

    /**
     * Getter of the player's pattern card
      * @return the pattern card bound to the player
     */
    public WindowPatternCard getPatternCard() {
        return myWindowPatternCard;
    }

    /**
     * Getter of the pattern chosen by the player
     * @return the pattern chosen by the player
     */
    public WindowPattern getPattern(){
        if(myWindowPatternCard == null)
            return null;
        return !isPatternFlipped ? myWindowPatternCard.getFrontPattern() : myWindowPatternCard.getRearPattern();
    }


    /**
     * Getter of player's private objective
     * @return player's private objective
     */
    public PrivateObjective getPrivateObjective() {
        return myPrivateObjective;
    }

    /**
     * Getter of player's placed dice matrix
     * @return a copy of the matrix with the dice placed by the player in the game
     */
    public Die[][] getPlacedDice(){
        return  cloneArray(placedDice);
    }

    /**
     * Getter of a die placed on the player's placedDice matrix in the given position
     * @param c a position of the placedDice matrix
     * @return the die placed in the given position
     */
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

    /**
     * @return a boolean flag which indicates whether the player has already placed a die
     * in this turn or not
     */
    public boolean getHasPlacedADieInThisTurn(){
        return hasPlacedADieInThisTurn;
    }

//endregion

    /**
     * @return the points earned by the player completing its private objective
     */
    public int countPrivateObjectivesPoints(){
        return myPrivateObjective.countPoints(this);
    }



    //region die placement

    /**
     * Places a die in the given position of the placedDice matrix
     * @param aDie the die to be placed
     * @param row the row index of the position on which the die must be placed
     * @param col the column index of the position on which the die must be placed
     */
    private void placeDieWithoutConstraints(Die aDie, int row, int col){
        placedDice[row][col] =    new Die(aDie);
        hasPlacedADieInThisTurn = true;
        hasEverPlacedADie =       true;

    }

    // Exceptions thrown if:
    //      a die is placed in an occupied position -> PositionOccupiedException
    //      a die placed doesn't match rules -> RuleViolatedExceptions
    //      a pattern constraint is violated -> PatternConstraintViolatedException

    /**
     * Places a die in the given position. Before placing it in the placedDice matrix,
     * executes some checks about rules, constraints and dice positions
     * @param aDie the die to be placed
     * @param row the row index of the position on which the die must be placed
     * @param col the column index of the position on which the die must be placed
     * @param checkPresence a boolean flag which indicates if there must be a die in
     *                      positions adjacent to the given one
     * @throws PositionOccupiedException if a die is asked to be placed in an occupied
     * position
     * @throws PatternConstraintViolatedException the constraint in the given position is
     * not compatible with the die asked to be placed
     * @throws RuleViolatedException if the player is trying to place a die without respecting rules
     */
    public void placeDie(Die aDie, int row, int col, boolean checkPresence) throws PositionOccupiedException, PatternConstraintViolatedException, RuleViolatedException {

        if(getHasPlacedADieInThisTurn())
            throw new RuleViolatedException("Player can't place more than a die at turn.");

        checkPlaceDie                     (aDie, row, col, true, true, hasEverPlacedADie && checkPresence);
        placeDieWithoutConstraints        (aDie, row, col);


    }

    /**
     * Moves some dice from the start positions to the end positions. Before move them in the placedDice matrix,
     * executes some checks about rules, constraints and dice positions
     * @param start list of starting move positions
     * @param end list of ending move positions
     * @param checkColour a boolean flag which indicates whether the move must respect
     *                    colour restrictions or not
     * @param checkValue a boolean flag which indicates whether the move must respect value
     *                   restrictions or not
     * @param checkPresence a boolean flag which indicates if there must be a die in
     *                      positions adjacent to the given one
     * @throws RuleViolatedException if the player is trying to move dice without
     * respecting rules
     * @throws PatternConstraintViolatedException if the player is trying to move dice
     * without respecting some constraint on its pattern
     * @throws PositionOccupiedException if the player is trying to move dice in already occupied
     * positions
     */
    public void moveDice(List<Coordinate> start, List<Coordinate> end, boolean checkColour, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException, PositionOccupiedException {
        Player copyTest = this.getMemento();
        List<Die> queue = new ArrayList<>();


        for (int i = 0; i < Math.min(start.size(), end.size()); i++) {
            Die aDie = copyTest.removeDie(start.get(i));
            if (aDie == null)
                throw new RuleViolatedException("There's no die to move at the specified cell");
            queue.add(aDie);
        }

        copyTest.hasEverPlacedADie = false;
        for (int i = 0; i < copyTest.placedDice.length && !copyTest.hasEverPlacedADie ; i++) {
            for (int j = 0; j < copyTest.placedDice[i].length && !copyTest.hasEverPlacedADie; j++) {
                if (copyTest.placedDice[i][j] != null) {
                    copyTest.hasEverPlacedADie = true;
                }
            }
        }

        for (int i = 0; i < queue.size(); i++) {

            if (copyTest.getPlacedDice(end.get(i)) != null)
                throw new RuleViolatedException("The destination cell is already occupied");

            copyTest.checkPlaceDie(queue.get(i),end.get(i), checkColour, checkValue, checkPresence);
            copyTest.placedDice[end.get(i).getRow()][end.get(i).getCol()] = queue.get(i);
        }
        copyTest.hasEverPlacedADie = true;
        this.saveMemento(copyTest);
    }

    /**
     * Removes a die from the placedDice matrix
     * @param row the row index of the position from which the die must be removed
     * @param col the column index of the position from which the die must be removed
     * @return the die removed from the given position
     */
    private Die removeDie(int row, int col){
        Die ret;
        ret = placedDice[row][col];
        placedDice[row][col] = null;
        return ret;
    }

    /**
     * Removes a die from the placedDice matrix
     * @param c coordinate containing row and column indexes of the position from which the
     *          die must be removed
     * @return the die removed from the given position
     */
    private Die removeDie(Coordinate c){
        return removeDie(c.getRow(), c.getCol());
    }

    /**
     * Gets the positions compatible for the given die to be placed
     * @param aDie the die asked to be placed
     * @return a list of compatible coordinate with the given die
     */
    public List<Coordinate> getCompatiblePositions(Die aDie){
        ArrayList<Coordinate> ret = new ArrayList<>();
        if(getHasPlacedADieInThisTurn())
            return ret;

        for(int row = 0; row < placedDice.length; row++){
            for(int col = 0; col < placedDice[0].length; col++){
                try{
                    checkPlaceDie(aDie, row, col, true, true, true);
                    ret.add(new Coordinate(row, col));
                } catch (Exception ignored) {
                    //because this method determines the possible die that can be placed into a
                    //location by the exception thrown
                }
            }
        }
        return ret;
    }

    /**
     * Executes checks about dice already placed on the placedDice matrix, rules and constraints
     * @param aDie the die asked to be placed or moved
     * @param row the row index of the position on which the die is asked to be placed or moved
     * @param col the column index of the position on which the die is asked to be placed or moved
     * @param checkColor a boolean flag which indicates whether the asked move must respect colour
     *                   restrictions or not
     * @param checkValue a boolean flag which indicates whether the asked move must respect value
     *                   restrictions or not
     * @param checkPresence a boolean flag which indicates if a die must be placed in
     *                      positions adjacent to the given one
     * @throws RuleViolatedException if the player asked a move which violates some rule
     * @throws PatternConstraintViolatedException if the player asked for a move which is
     * not compatible with a pattern constraint
     * @throws PositionOccupiedException if the player asked for a move which involves to
     * place a die in a already occupied position
     */
    private void checkPlaceDie(Die aDie, int row, int col, boolean checkColor, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException, PositionOccupiedException {

        if(placedDice[row][col]!= null) {
            throw new PositionOccupiedException("A die has already been placed here");
        }


        if(!hasEverPlacedADie && row !=0 && row != getPattern().getHeight()-1 && col != 0 && col != getPattern().getWidth()-1){
            throw new RuleViolatedException("Each player’s first die of the game must be placed on an edge or corner space");

        }

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

        boolean isThereAnAdjacentDie = isThereAnAdjacentDie(row, col);


        if(checkPresence && !isThereAnAdjacentDie && hasEverPlacedADie) {
            throw new RuleViolatedException("Die must be placed near an already placed die!");
        }

        if(!checkPresence && isThereAnAdjacentDie){
            throw new RuleViolatedException("Die must be placed away from other dice");
        }

    }


    /**
     * Executes checks about dice already placed on the placedDice matrix, rules and constraints
     * @param aDie the die asked to be placed or moved
     * @param c the coordinate containing row and column indexes of the position
     *          on which the die is asked to be placed or moved
     * @param checkColor a boolean flag which indicates whether the asked move must respect colour
     *                   restrictions or not
     * @param checkValue a boolean flag which indicates whether the asked move must respect value
     *                   restrictions or not
     * @param checkPresence a boolean flag which indicates if a die must be placed in
     *                      positions adjacent to the given one
     * @throws RuleViolatedException if the player asked a move which violates some rule
     * @throws PatternConstraintViolatedException if the player asked for a move which is
     * not compatible with a pattern constraint
     * @throws PositionOccupiedException if the player asked for a move which involves to
     * place a die in a already occupied position
     */
    private void checkPlaceDie(Die aDie, Coordinate c, boolean checkColor, boolean checkValue, boolean checkPresence) throws RuleViolatedException, PatternConstraintViolatedException, PositionOccupiedException {
        checkPlaceDie(aDie, c.getRow(), c.getCol(), checkColor, checkValue, checkPresence);
    }

    /**
     * Gets a list of the dice placed in positions adjacent to the given one
     * @param placedDice the placedDice matrix of the player
     * @param row the row index of the position asked to be looked for adjacent dice
     * @param col the column index of the position asked to be looked for adjacent dice
     * @return a list of the dice placed in positions adjacent to the given one
     */
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

    /**
     * Check if there is a die in a position adjacent to the given one. This methods check if
     * there is a die also on diagonals
     * @param row the row index of the position asked to be looked for adjacent dice
     * @param col the column index of the position asked to be looked for adjacent dice
     * @return a boolean flag which indicates whether there is a die in a position adjacent
     * to the given one or not
     */
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

    /**
     *
     * @return the number of empty cells in the player's placedDice matrix
     */
    public int getEmptyCells(){
        int ret = 0;

        for(Die[] row : placedDice)
            for(Die die : row)
                if(die == null)
                    ret++;
        return ret;
    }

    /**
     * Re-initializes player's flag which indicates if it placed a die in the turn or not
     */
    public void endTurn() {
        hasPlacedADieInThisTurn = false;
    }

    //endregion

    /**
     * Sends events to player's view
     * @param events list of events to be sent to player's view
     */
    public void update(Event... events) {
        if(!isConnected())
            return;
        try {
            for(Event aEvent : events){
                myView.update(aEvent);
            }
        }catch (IOException ex){
            //no exception can be raised if player is connected since there's a
            //view on the server that receives its messages and passed it to the real view.
            assert(false);
        }

    }

    //region connection

    /**
     * Sets to false the flag isConnected, interrupts the execution of player's view proxy
     * and sets player's view reference to null
     */
    public void disconnectView(){
        isConnected = false;
        if( this.myView instanceof Thread)
            ((Thread) myView).interrupt();
        myView = null;
    }

    /**
     * Sets to true the flag isConnected. If the player is still connected, interrupts the
     * execution of player's old view proxy and sets player's new view reference
     * @param myView player's new view reference
     */
    public void reconnectView(IView myView){
        if( this.myView instanceof Thread)
            ((Thread) this.myView).interrupt();
        this.myView = myView;
        isConnected = true;
    }

    //endregion

    //region object override

    /**
     *
     * @param o player to be compared to this
     * @return a boolean flag which indicates whether the given object's state is equal to
     * player's state or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return isPatternFlipped == player.isPatternFlipped &&
                hasPlacedADieInThisTurn == player.hasPlacedADieInThisTurn &&
                isConnected == player.isConnected &&
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
        return "---------------------\n" +
                getName() +
                "'s situation ...\n" +
                Card.drawNear("Private objective : \n" + (myPrivateObjective == null ? "Ehi! No peeking allowed!!" : myPrivateObjective.toString()),
                        "Player game board: \n\n" + stringifyPlayerGameBoard()) +
                "---------------------\n";
    }

    /**
     *
     * @return a string representation of player's pattern
     */
    private String stringifyPlayerGameBoard() {
        StringBuilder aBuilder = new StringBuilder();
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
                        //else take the constrain representation itself
                        tmp = constraintsMatrix[r][c].toString();
                    }
                    aBuilder.append(constraintsMatrix[r][c].getColour().colourBackground(tmp));
                }
                aBuilder.append("\n");
            }
        }
        return aBuilder.toString();
    }

//endregion
}