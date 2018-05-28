package project.ing.soft.model.cards.objectives;

import project.ing.soft.Settings;
import project.ing.soft.model.StringBoxBuilder;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.Player;

import java.io.Serializable;

public abstract class ObjectiveCard implements Serializable, Card{

    private String title;
    private String description;
    private String imgPath;
    private int points;

    public ObjectiveCard(String title, String description, String imgPath, int points) {

        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.points = points;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImgPath() {
        return imgPath;
    }

    @Override
    public String getDescription() {
        return description;
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
        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.DOUBLELINESQUAREANGLE(),Settings.instance().getTEXT_CARD_WIDTH(), Settings.instance().getTEXT_CARD_HEIGHT());
        aBuilder.appendInAboxToTop(getTitle());
        aBuilder.appendToTop(getDescription());
        aBuilder.prependInAboxToBottom("Punti: "+ getPoints());
        return aBuilder.toString();
    }
}
