package projectIngSoft;

import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPattern;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.View.IView;
import projectIngSoft.events.Event;
import projectIngSoft.exceptions.PatternConstraintViolatedException;
import projectIngSoft.exceptions.PositionOccupiedException;
import projectIngSoft.exceptions.RuleViolatedException;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String            name;

    private boolean                 isPatternFlipped;
    private WindowPatternCard       myWindowPatternCard;
    private List<WindowPatternCard> possiblePatternCards;

    private PrivateObjective        myPrivateObjective;
    private Die[][]                 placedDice;

    private IView                   myView;
    private boolean                 alreadyPlacedADie;


    //In order to let the player use patterns with non-predefined dimensions
    //the player placedDice matrix is created when patternCard is chosen.
    public Player(String name, IView aView) {
        this.name =  name;
        this.placedDice = new Die[1][1];
        this.myView = aView;
        this.alreadyPlacedADie = false;
    }

    public String getName() {
        return name;
    }

    public void setPatternCard(WindowPatternCard aPatternCard) {
        this.myWindowPatternCard = aPatternCard;
        int width =  getPattern().getWidth();
        int height =  getPattern().getHeight();
        this.placedDice = new Die[height][width];
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

    public boolean getAlreadyPlacedADie(){
        return alreadyPlacedADie;
    }





    public void setPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;

    }


    // Exceptions thrown if:
    //      a die is placed in an occupied position -> PositionOccupiedException
    //      a die placed doesn't match rules -> RuleViolatedExceptions
    //      a pattern constraint is violated -> PatternConstraintViolatedException
    public void placeDie(Die aDie, int row, int col) throws PositionOccupiedException, PatternConstraintViolatedException, RuleViolatedException {
        if(placedDice[row][col]!= null) {
            throw new PositionOccupiedException("A die has already been placed here");
        }
        checkConstraints(row, col, aDie);
        checkAdjacents(getAdjacents(getPlacedDice(),row,col),aDie);
        placeDieWithoutConstraints(aDie, row, col);
    }


    public void placeDieWithoutConstraints(Die aDie, int row, int col){
        placedDice[row][col] = new Die(aDie);
        alreadyPlacedADie = true;
    }

    private void checkConstraints(int rowIndex, int colIndex, Die aDie) throws PatternConstraintViolatedException {
        Constraint actualConstraint = getPattern().getConstraintsMatrix()[rowIndex][colIndex];
        if((!actualConstraint.getColour().equals(aDie.getColour()) && !actualConstraint.getColour().equals(Colour.WHITE))|| (actualConstraint.getValue()!=aDie.getValue() && actualConstraint.getValue() != 0))
            throw new PatternConstraintViolatedException("Ehi, you cheater! You are violating a constraint on your pattern! Try again, and play fairly!");
    }


    private void checkAdjacents(List<Die> adjacents, Die choseDie) throws RuleViolatedException {
        for(int i = 0; i < adjacents.size(); i++){
            Die placedDie = adjacents.get(i);
            if(placedDie != null && (placedDie.getValue() == choseDie.getValue() || placedDie.getColour().equals(choseDie.getColour()))) {
                throw new RuleViolatedException("Ehi! You are trying to place a die with the same colour or the same value than an adjacent die. You can't do whatever you want! You must follow the rules");
            }
        }
    }

    private ArrayList<Die> getAdjacents(Die[][] placedDice, int row, int col){
        ArrayList<Die> ret = new ArrayList<>();

        if(col + 1 < placedDice[row].length)
            ret.add(placedDice[row][col+1]);
        if(col > 0)
            ret.add(placedDice[row][col-1]);
        if(row + 1 < placedDice.length)
            ret.add(placedDice[row+1][col]);
        if(row > 0)
            ret.add(placedDice[row-1][col]);

        return ret;
    }


    public void resetDieFlag() {
        alreadyPlacedADie = false;
    }

    public void update(IGameManager updatedModel, Event event) {
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
        aBuilder.append(name);
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