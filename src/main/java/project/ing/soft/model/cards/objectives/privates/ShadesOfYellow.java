package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

public class ShadesOfYellow extends PrivateObjective {

    public ShadesOfYellow(){
        super(  Settings.ObjectivesProperties.ShadesOfYellow.getTitle(),
                Settings.ObjectivesProperties.ShadesOfYellow.getDescription(),
                Settings.ObjectivesProperties.ShadesOfYellow.getPath(),
                Settings.ObjectivesProperties.ShadesOfYellow.getPoints(),
                Colour.YELLOW);
    }
}
