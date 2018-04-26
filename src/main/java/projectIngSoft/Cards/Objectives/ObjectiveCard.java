package projectIngSoft.Cards.Objectives;

import projectIngSoft.Cards.Card;
import projectIngSoft.Player;

import java.io.Serializable;

public abstract class ObjectiveCard extends Card implements Serializable{

    protected int points;

    public ObjectiveCard(String title, String description, int points){
        super(title, description);
        this.points = points;
    }



    public int getPoints(){
        return points;
    }

    public int countPoints(Player window){
        return points*checkCondition(window);
    }

    public abstract int checkCondition(Player window);

}
