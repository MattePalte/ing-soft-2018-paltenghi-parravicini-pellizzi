package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective.
 */
public class ShadesOfBlue extends PrivateObjective {

    /**
     * SHADES_OF_BLUE constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfBlue(){
        super(  Settings.ObjectivesProperties.SHADES_OF_BLUE.getTitle(),
                Settings.ObjectivesProperties.SHADES_OF_BLUE.getDescription(),
                Settings.ObjectivesProperties.SHADES_OF_BLUE.getPath(),
                Settings.ObjectivesProperties.SHADES_OF_BLUE.getPoints(),
                Colour.BLUE);
    }
}
