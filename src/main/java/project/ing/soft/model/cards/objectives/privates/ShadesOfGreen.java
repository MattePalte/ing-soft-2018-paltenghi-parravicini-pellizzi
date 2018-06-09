package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective
 */
public class ShadesOfGreen extends PrivateObjective {

    /**
     * ShadesOfGreen constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfGreen(){
        super(  Settings.ObjectivesProperties.ShadesOfGreen.getTitle(),
                Settings.ObjectivesProperties.ShadesOfGreen.getDescription(),
                Settings.ObjectivesProperties.ShadesOfGreen.getPath(),
                Settings.ObjectivesProperties.ShadesOfGreen.getPoints(),
                Colour.GREEN);
    }

}
