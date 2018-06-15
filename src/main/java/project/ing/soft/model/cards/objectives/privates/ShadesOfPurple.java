package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective
 */
public class ShadesOfPurple extends PrivateObjective {

    /**
     * SHADES_OF_PURPLE constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfPurple(){
        super(  Settings.ObjectivesProperties.SHADES_OF_PURPLE.getTitle(),
                Settings.ObjectivesProperties.SHADES_OF_PURPLE.getDescription(),
                Settings.ObjectivesProperties.SHADES_OF_PURPLE.getPath(),
                Settings.ObjectivesProperties.SHADES_OF_PURPLE.getPoints(),
                Colour.VIOLET);
    }

}
