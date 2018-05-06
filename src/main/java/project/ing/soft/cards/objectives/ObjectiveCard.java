package project.ing.soft.cards.objectives;

import project.ing.soft.cards.Card;
import project.ing.soft.Player;

import java.io.Serializable;

public abstract class ObjectiveCard extends Card implements Serializable{

    protected int points;

    public ObjectiveCard(String title, String description, String resourcePath, int points ){
        super(title, description, resourcePath);
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
