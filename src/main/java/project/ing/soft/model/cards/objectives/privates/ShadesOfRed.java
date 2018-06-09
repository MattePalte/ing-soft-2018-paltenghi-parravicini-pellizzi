package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective
 */
public class ShadesOfRed extends PrivateObjective {

    /**
     * ShadesOfRed constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfRed(){
        super(  Settings.ObjectivesProperties.ShadesOfRed.getTitle(),
                Settings.ObjectivesProperties.ShadesOfRed.getDescription(),
                Settings.ObjectivesProperties.ShadesOfRed.getPath(),
                Settings.ObjectivesProperties.ShadesOfRed.getPoints(),
                Colour.RED);
    }

}
