package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

public class ShadesOfGreen extends PrivateObjective {

    public ShadesOfGreen(){
        super(  Settings.ObjectivesProperties.ShadesOfGreen.getTitle(),
                Settings.ObjectivesProperties.ShadesOfGreen.getDescription(),
                Settings.ObjectivesProperties.ShadesOfGreen.getPath(),
                Settings.ObjectivesProperties.ShadesOfGreen.getPoints(),
                Colour.GREEN);
    }

}
