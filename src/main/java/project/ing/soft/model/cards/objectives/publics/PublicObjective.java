package project.ing.soft.model.cards.objectives.publics;

import project.ing.soft.model.cards.objectives.ObjectiveCard;

public abstract class PublicObjective extends ObjectiveCard {
    public PublicObjective(String title, String description, int points, String resourcePath){
        super(title,description,resourcePath, points);
    }
}
