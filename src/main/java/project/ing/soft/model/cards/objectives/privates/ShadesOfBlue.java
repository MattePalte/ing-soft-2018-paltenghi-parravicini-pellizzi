package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective.
 */
public class ShadesOfBlue extends PrivateObjective {

    /**
     * ShadesOfBlue constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfBlue(){
        super(  Settings.ObjectivesProperties.ShadesOfBlue.getTitle(),
                Settings.ObjectivesProperties.ShadesOfBlue.getDescription(),
                Settings.ObjectivesProperties.ShadesOfBlue.getPath(),
                Settings.ObjectivesProperties.ShadesOfBlue.getPoints(),
                Colour.BLUE);
    }
}
