package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.model.cards.objectives.ObjectiveCard;

import java.io.Serializable;

/**
 * PrivateObjective class contains all basic information and methods for all public objective
 * cards. This is an extension of class ObjectiveCard.
 */
public abstract class PublicObjective extends ObjectiveCard implements Serializable {
    /**
     * PublicObjective constructor
     * @param title of the objective card
     * @param description readable information about how to count points
     * @param points points given to a player every time this objective is completed
     * @param resourcePath path of the card image
     */
    public PublicObjective(String title, String description, int points, String resourcePath){
        super(title,description,resourcePath, points);
    }
}
