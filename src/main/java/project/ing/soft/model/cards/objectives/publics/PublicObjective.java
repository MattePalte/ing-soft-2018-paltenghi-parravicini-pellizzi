package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.model.cards.objectives.ObjectiveCard;

import java.io.Serializable;

public abstract class PublicObjective extends ObjectiveCard implements Serializable {
    public PublicObjective(String title, String description, int points, String resourcePath){
        super(title,description,resourcePath, points);
    }
}
