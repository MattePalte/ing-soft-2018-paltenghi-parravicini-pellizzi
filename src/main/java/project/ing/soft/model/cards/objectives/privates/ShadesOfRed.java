package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective
 */
public class ShadesOfRed extends PrivateObjective {

    /**
     * SHADES_OF_RED constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfRed(){
        super(  Settings.ObjectivesProperties.SHADES_OF_RED.getTitle(),
                Settings.ObjectivesProperties.SHADES_OF_RED.getDescription(),
                Settings.ObjectivesProperties.SHADES_OF_RED.getPath(),
                Settings.ObjectivesProperties.SHADES_OF_RED.getPoints(),
                Colour.RED);
    }

}
