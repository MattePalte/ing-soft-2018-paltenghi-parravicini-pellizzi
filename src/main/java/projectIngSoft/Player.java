package projectIngSoft;

import projectIngSoft.Cards.Constraint;
import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.ToolCards.ToolCard;
import projectIngSoft.Cards.WindowPattern;
import projectIngSoft.Cards.WindowPatternCard;
import projectIngSoft.Controller.IController;
import projectIngSoft.GameManager.IGameManager;
import projectIngSoft.View.IView;
import projectIngSoft.exceptions.AlreadyPlacedADieException;
import projectIngSoft.exceptions.PositionOccupiedException;

public class Player {
    private final String     name;
    private WindowPatternCard myWindowPatternCard;
    private PrivateObjective myPrivateObjective;
    private Die[][]          placedDice;
    private boolean isPatternFlipped;
    private IView myView;
    private boolean alreadyPlacedADie;

    //Constructor
    public Player(String name, IView aView) {
        this.name = new String(name);
        //altrimenti errore
        this.placedDice = new Die[1][1];
        this.myView = aView;
        this.alreadyPlacedADie = false;
    }

    //---------------------- OBSERVERS -------------------------
    //@assignable nothing
    public String getName() {
        return new String(name);
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
        return !isPatternFlipped ? myWindowPatternCard.getFrontPattern() : myWindowPatternCard.getRearPattern();
    }

    public boolean getAlreadyPlacedADie(){
        return alreadyPlacedADie;
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
        StringBuilder aBuilder =  new StringBuilder(
                "---------------------\n" +
                name + "'s situation ..."+
                //"\nmyWindowPatternCard ->\n" + myWindowPatternCard +
                "\nPrivObj : " + myPrivateObjective.getTitle()+"\n");
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
        aBuilder.append("---------------------");
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
            alreadyPlacedADie = true;
            return true;
        } catch (IndexOutOfBoundsException | CloneNotSupportedException e) {
            return false;
        }
    }

    public boolean playToolCard(ToolCard aToolCard){
        return true;
    }

    public void takeTurn() throws Exception {
        alreadyPlacedADie = false;
        myView.takeTurn();
    }

    public void updateView(IGameManager updatedModel) {
        //TODO: warning to rep exposition -> implement model Clone of Model
        myView.update(updatedModel);
    }

    public void giveControllerToTheView(IController aController) {
        //TODO: warning to rep exposition -> implement model Clone of Controller
        myView.attachController(aController);
    }

    //---------------------- NOT YET IMPLEMENTED METHODS - TO DISCUSS -------------------------

    public String getPlayerSecurityCode(){
        return new String("Codice");
    }

}