package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

/**
 * Specific implementation of a PrivateObjective
 */
public class ShadesOfPurple extends PrivateObjective {

    /**
     * ShadesOfPurple constructor. It takes information about the specific objective from
     * class Settings
     */
    public ShadesOfPurple(){
        super(  Settings.ObjectivesProperties.ShadesOfPurple.getTitle(),
                Settings.ObjectivesProperties.ShadesOfPurple.getDescription(),
                Settings.ObjectivesProperties.ShadesOfPurple.getPath(),
                Settings.ObjectivesProperties.ShadesOfPurple.getPoints(),
                Colour.VIOLET);
    }

}
