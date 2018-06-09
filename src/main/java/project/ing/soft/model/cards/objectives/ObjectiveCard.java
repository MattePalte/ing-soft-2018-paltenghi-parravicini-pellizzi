package project.ing.soft.model.cards.objectives;

import project.ing.soft.Settings;
import project.ing.soft.model.StringBoxBuilder;
import project.ing.soft.model.cards.Card;
import project.ing.soft.model.Player;

import java.io.Serializable;

/**
 * ObjectiveCard class contains all the basic information and methods for objective cards
 */
public abstract class ObjectiveCard implements Serializable, Card{

    private String title;
    private String description;
    private String imgPath;
    private int points;

    /**
     * ObjectiveCard constructor
     * @param title of the objective card
     * @param description readable information about how to count points
     * @param imgPath path of the card image
     * @param points points given to a player every time this objective is completed
     */
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

    /**
     * This getter returns how many points the player is given by completing the objective once
     * @return value of "points" attribute
     */
    public int getPoints(){
        return points;
    }

    /**
     * This method returns the point amount obtained by the given player due to
     * objective achievement
     * @param window the player who is counting points on its window
     * @return points counted to have completed this objective
     */
    public int countPoints(Player window){
        return points*checkCondition(window);
    }

    /**
     * This method verifies if the objective is completed by the given player
     * and returns how many times he managed to complete it
     * @param window the player who is counting points on its window
     * @return how many times the condition to complete this objective is achieved
     */
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
