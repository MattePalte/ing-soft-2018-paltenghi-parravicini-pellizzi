package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

public class ShadesOfPurple extends PrivateObjective {

    public ShadesOfPurple(){
        super(  Settings.ObjectivesProperties.ShadesOfPurple.getTitle(),
                Settings.ObjectivesProperties.ShadesOfPurple.getDescription(),
                Settings.ObjectivesProperties.ShadesOfPurple.getPath(),
                Settings.ObjectivesProperties.ShadesOfPurple.getPoints(),
                Colour.VIOLET);
    }

}
