package project.ing.soft.model.cards.toolcards;

import project.ing.soft.exceptions.ToolCardApplicationException;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;
import project.ing.soft.model.gamemanager.IGameManager;

public abstract class SingleInterationToolcard extends ToolCard {

    public SingleInterationToolcard(String aTitle, String description, Colour aColour, String resourcePath) {
        super(aTitle, description, aColour, resourcePath);
    }
}
