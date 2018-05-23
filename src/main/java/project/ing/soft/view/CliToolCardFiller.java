package project.ing.soft.view;

import project.ing.soft.exceptions.UserInterruptActionException;
import project.ing.soft.model.Coordinate;
import project.ing.soft.model.Die;
import project.ing.soft.model.cards.toolcards.*;
import project.ing.soft.model.gamemanager.IGameManager;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CliToolCardFiller implements IToolCardFiller {
    private static final String REQUEST_FOR_EMPTY_CELL_POSITION = "Enter an empty cell's position to move it";
    private NonBlockingScanner scanner;
    private PrintStream out;
    private IGameManager localCopyOfModel;

    public CliToolCardFiller(NonBlockingScanner scanner, PrintStream out, IGameManager localCopyOfModel) {
        this.scanner = scanner;
        this.out = out;
        this.localCopyOfModel = localCopyOfModel;
    }

    public void updateLocalCopyOfModel(IGameManager localCopyOfModel) {
        this.localCopyOfModel = localCopyOfModel;
    }

    private int waitForUserInput(int lowerBound , int upperBound) throws UserInterruptActionException, InterruptedException {
        int ret = 0;
        boolean err;

        do{
            err = false;
            try{
                ret = Integer.valueOf(scanner.readLine());
            }
            catch( NumberFormatException e){
                err = true;
            }
            err = err || ret < lowerBound || ret > upperBound;

            if(err){
                if(scanner.readLine().startsWith("q"))
                    throw new UserInterruptActionException();
                out.println("You entered a value that does not fit into the correct interval. Enter q to interrupt the operation");

            }
        }while(err);




        return ret;
    }

    private Coordinate chooseDieCoordinate (String caption) throws UserInterruptActionException, InterruptedException {
        out.println(caption);
        int row ;
        int col ;
        out.println("Row Index [0 - 3]");
        row = waitForUserInput(0, 3);
        out.println("Col Index [0 - 4]");
        col = waitForUserInput(0, 4);
        return new Coordinate(row, col);

    }

    private Object chooseFrom(List objs) throws UserInterruptActionException, InterruptedException {
        return objs.get(chooseIndexFrom(objs));
    }

    private int chooseIndexFrom(List objs) throws UserInterruptActionException, InterruptedException {

        out.println(String.format("Enter a number between 0 and %d to select:", objs.size()-1));
        for (int i = 0; i < objs.size() ; i++) {
            out.println(String.format("[%d] for %s", i, objs.get(i).toString()));
        }
        return waitForUserInput(0, objs.size()-1);

    }
    

    public void fill(ToolCard aToolCard) throws UserInterruptActionException, InterruptedException {
        aToolCard.fillFirst(this);
    }

    @Override
    public void fill(AlesatoreLaminaRame aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        aToolCard.setStartPosition(chooseDieCoordinate("Enter which die you want to move"));
        aToolCard.setEndPosition(chooseDieCoordinate(REQUEST_FOR_EMPTY_CELL_POSITION));
    }

    @Override
    public void fill(DiluentePastaSalda aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        out.println("Choose a die to take back to the dicebag: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfModel.getDraftPool());
        aToolCard.setChosenDie(chosenDie);
    }

    @Override
    public void fill(Lathekin aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        aToolCard.setFirstDieStartPosition(chooseDieCoordinate("Enter which is the first die you want to move"));
        aToolCard.setFirstDieEndPosition(chooseDieCoordinate(REQUEST_FOR_EMPTY_CELL_POSITION));
        aToolCard.setSecondDieStartPosition(chooseDieCoordinate("Enter which is the second die you want to move"));
        aToolCard.setSecondDieEndPosition(chooseDieCoordinate(REQUEST_FOR_EMPTY_CELL_POSITION));
    }

    @Override
    public void fill(Martelletto aToolCard) throws InterruptedException, UserInterruptActionException {
        //The toolcard Martelleto does not need any parameter
    }

    @Override
    public void fill(PennelloPastaSalda aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        Die chosenDie;
        chosenDie =  (Die) chooseFrom(localCopyOfModel.getDraftPool());
        aToolCard.setToRoll(chosenDie);
    }

    @Override
    public void fill(PennelloPerEglomise aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        aToolCard.setStartPosition(chooseDieCoordinate("Enter which die you want to move"));
        aToolCard.setEndPosition(chooseDieCoordinate(REQUEST_FOR_EMPTY_CELL_POSITION));
    }

    @Override
    public void fill(PinzaSgrossatrice aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        Die chosenDie;
        boolean toBeIncreased;
        chosenDie =  (Die) chooseFrom(localCopyOfModel.getDraftPool());
        aToolCard.setChoosenDie(chosenDie);
        toBeIncreased = (chooseIndexFrom(List.of("Decrease its value", "Increase its value")) == 1);
        aToolCard.setToBeIncreased(toBeIncreased);
    }

    @Override
    public void fill(RigaSughero aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        out.println("Choose a die from the draftpool: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfModel.getDraftPool());
        aToolCard.setChosenDie(chosenDie);
        Coordinate chosenPosition = chooseDieCoordinate("Choose a position away from other dice: ");
        aToolCard.setPosition(chosenPosition);

    }

    @Override
    public void fill(StripCutter aToolCard) {
        //the StripCutter Toolcard does not need parameters
    }

    @Override
    public void fill(TaglierinaManuale aToolCard) throws InterruptedException, UserInterruptActionException {
        ArrayList<Coordinate> positions = new ArrayList<>();
        ArrayList<Coordinate> moveTo = new ArrayList<>();

        out.println(aToolCard);
        out.println("Choose a die from the roundtracker: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfModel.getRoundTracker().getDiceLeftFromRound());
        aToolCard.setDieFromRoundTracker(chosenDie);
 

        do{
            positions.add(chooseDieCoordinate("Choose the position of a " + chosenDie.getColour() + " placed die in your pattern"));
            moveTo.add(chooseDieCoordinate("Choose where you want to move the die you have just chosen"));
            out.println("With this toolcard you can move up to 2 die. Do you want to select die? [y/n]");
        }while(positions.size()<2 && scanner.readLine().startsWith("y"));

        aToolCard.setDiceChosen(positions);
        aToolCard.setMoveTo(moveTo);


    }

    @Override
    public void fill(TaglierinaCircolare aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        Die chosenDieFromDraft;
        Die chosenDieFromRoundTracker;
        out.println("Chose from Draft:");
        chosenDieFromDraft =  (Die) chooseFrom(localCopyOfModel.getDraftPool());
        aToolCard.setDieFromDraft(chosenDieFromDraft);
        out.println("Chose from RoundTracker:");
        chosenDieFromRoundTracker =  (Die) chooseFrom(localCopyOfModel.getRoundTracker().getDiceLeftFromRound());
        aToolCard.setDieFromRoundTracker(chosenDieFromRoundTracker);
    }

    @Override
    public void fill(TamponeDiamantato aToolCard) throws InterruptedException, UserInterruptActionException {
        out.println(aToolCard);
        out.println("Choose a die from the draftpool: ");
        Die chosenDie = (Die) chooseFrom(localCopyOfModel.getDraftPool());
        aToolCard.setChosenDie(chosenDie);
    }

    @Override
    public void fill(TenagliaRotelle aToolCard) throws InterruptedException, UserInterruptActionException {
        //The TenagliaRotelle does not need any parameter
    }

}