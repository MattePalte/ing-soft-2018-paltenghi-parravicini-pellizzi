package project.ing.soft.model.cards.objectives;

import project.ing.soft.model.StringBoxBuilder;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.Player;

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

    @Override
    public String toString() {
        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.DOUBLELINESQUAREANGLE(),Card.WIDTH_CARD, Card.HEIGHT_CARD);
        aBuilder.appendInAboxToTop(getTitle());
        aBuilder.appendToTop(getDescription());
        aBuilder.prependInAboxToBottom("Punti: "+ getPoints());
        return aBuilder.toString();
    }
}
