package projectIngSoft;

import java.util.ArrayList;

public class RoundTracker {
    private ArrayList<Die> diceLeft;
    private static RoundTracker instance;

    private RoundTracker(){
        diceLeft = new ArrayList<Die>();
    }

    public static RoundTracker getInstance(){
        if(instance == null)
            instance = new RoundTracker();
        return instance;
    }
}
