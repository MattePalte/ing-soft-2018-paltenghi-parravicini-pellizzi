package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective
 */
public class ShadesOfGreen extends PrivateObjective {

    /**
     * SHADES_OF_GREEN constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfGreen(){
        super(  Settings.ObjectivesProperties.SHADES_OF_GREEN.getTitle(),
                Settings.ObjectivesProperties.SHADES_OF_GREEN.getDescription(),
                Settings.ObjectivesProperties.SHADES_OF_GREEN.getPath(),
                Settings.ObjectivesProperties.SHADES_OF_GREEN.getPoints(),
                Colour.GREEN);
    }

}
