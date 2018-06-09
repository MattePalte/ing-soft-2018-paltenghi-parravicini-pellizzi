package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

public class ShadesOfBlue extends PrivateObjective {

    public ShadesOfBlue(){
        super(  Settings.ObjectivesProperties.ShadesOfBlue.getTitle(),
                Settings.ObjectivesProperties.ShadesOfBlue.getDescription(),
                Settings.ObjectivesProperties.ShadesOfBlue.getPath(),
                Settings.ObjectivesProperties.ShadesOfBlue.getPoints(),
                Colour.BLUE);
    }
}
