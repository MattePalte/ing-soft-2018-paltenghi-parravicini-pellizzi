package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

public class ShadesOfYellow extends PrivateObjective {

    public ShadesOfYellow(){
        super(  Settings.ObjectivesProperties.SHADES_OF_YELLOW.getTitle(),
                Settings.ObjectivesProperties.SHADES_OF_YELLOW.getDescription(),
                Settings.ObjectivesProperties.SHADES_OF_YELLOW.getPath(),
                Settings.ObjectivesProperties.SHADES_OF_YELLOW.getPoints(),
                Colour.YELLOW);
    }
}
