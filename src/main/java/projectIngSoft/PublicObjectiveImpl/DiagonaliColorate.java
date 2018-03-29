package projectIngSoft.PublicObjectiveImpl;

import projectIngSoft.*;

import java.util.HashMap;
import java.util.Map;

public class DiagonaliColorate extends PublicObjective {

    public DiagonaliColorate(){
        super("Diagonali Colorate", "Conta il numero di dadi dello stesso colore posizionati diagonalmente l'uno rispetto all'altro", 1);
    }


    public int checkCondition(WindowFrame window) {
        WindowPattern pattern = window.getPattern();
        Die[][] placedDice = window.getPlacedDice();
        Integer[][] countedDice = new Integer[pattern.getWidth()][pattern.getHeight()];
        Map<Colour, Integer> colorQty = new HashMap<>();
        int counter;
        Colour actual;

        // Initializing colorQty map
        Colour.validColours().forEach( colour -> colorQty.put(colour,0));

        // Initializing countedDice matrix
        for (Integer[] row : countedDice)
            for (Integer num : row)
                num = 0;

        // Starting from the first column, check all the bottomleft->topright diagonals
        for(int initRow = 0; initRow < pattern.getHeight(); initRow++) {
            actual = placedDice[initRow][0].getColour();
            counter = checkDiagonalsBL2TR(initRow,0, window, countedDice);
            counter -= sumCountedDice(countedDice, pattern.getHeight(), pattern.getWidth());
            if (counter > 0)
                colorQty.put(actual,colorQty.get(actual) + counter);
            }

        // Starting from the last row, check all the bottomleft->topright diagonals
        for(int initCol = 1; initCol < pattern.getWidth(); initCol++) {
            actual = placedDice[pattern.getHeight() - 1][initCol].getColour();
            counter = checkDiagonalsBL2TR(pattern.getHeight() - 1, initCol, window, countedDice);
            counter -= sumCountedDice(countedDice, pattern.getHeight(), pattern.getWidth());
            if (counter > 0)
                colorQty.put(actual,colorQty.get(actual) + counter);
        }

        // Starting from the first row, check all the topleft->bottomright diagonals
        for(int initCol = 0; initCol < pattern.getWidth(); initCol ++){
            actual = placedDice[0][initCol].getColour();
            counter = checkDiagonalsTL2BR(0, initCol, window, countedDice);
            counter -= sumCountedDice(countedDice, pattern.getHeight(), pattern.getWidth());
            if(counter > 0)
                    colorQty.put(actual, colorQty.get(actual) + counter);
        }

        // Starting from the first column, check all the topleft->bottomright diagonals
        for(int initRow = 0; initRow < pattern.getHeight(); initRow++){
            actual = placedDice[initRow][0].getColour();
            counter = checkDiagonalsTL2BR(initRow, 0, window, countedDice);
            counter -= sumCountedDice(countedDice, pattern.getHeight(), pattern.getWidth());
            if(counter > 0)
                colorQty.put(actual, colorQty.get(actual) + counter);
        }

        return 0;
    }

    // Function used by checkCondition method which returns the sum of the values reduced by 1 of the matrix countedDice
    private int sumCountedDice(Integer[][] countedDice, int maxHeight, int maxWidth){
        int sumCounted = 0;
        for(int row = 0; row < maxHeight; row++)
            for(int col = 0; col < maxWidth; col++)
                sumCounted += (countedDice[row][col] - 1);
        return sumCounted;
    }

    // Function used by checkCondition method which returns the number of dice of the same colour on a diagonal from bottomleft to topright
    private int checkDiagonalsBL2TR(int initRow, int initCol, WindowFrame window, Integer[][] countedDice){
        WindowPattern pattern = window.getPattern();
        Die[][] placedDice = window.getPlacedDice();
        int row = initRow - 1;
        int col = initCol + 1;
        int counter = 1;
        Colour actual = placedDice[row + 1][col - 1].getColour();

        while(row >= 0 && col < pattern.getWidth() && placedDice[row][col].getColour() == actual){
            countedDice[row][col]++;
            counter++;
            row--;
            col++;
        }
        return counter;
    }

    // Function used by checkCondition method which returns the number of dice of the same colour on a diagonal from topleft to bottomright
    private int checkDiagonalsTL2BR(int initRow, int initCol, WindowFrame window, Integer[][] countedDice){
        WindowPattern pattern = window.getPattern();
        Die[][] placedDice = window.getPlacedDice();
        int row = initRow + 1;
        int col = initCol + 1;
        int counter = 1;
        Colour actual = placedDice[row + 1][col - 1].getColour();

        while(row >= 0 && col < pattern.getWidth() && placedDice[row][col].getColour() == actual){
            countedDice[row][col]++;
            counter++;
            row++;
            col++;
        }
        return counter;
    }
}
