package projectIngSoft.Cards.Objectives;

import projectIngSoft.Cards.Card;
import projectIngSoft.Player;

public abstract class ObjectiveCard extends Card {

    private int points;

    public ObjectiveCard(String title, String description, int points){
        super(title, description);
        this.points = points;
    }



    public int getPoints(){
        return points;
    }

    public int countPoints(Player window){

        return getPoints()*checkCondition(window);
    }

    public abstract int checkCondition(Player window);

}
