package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;

public class ShadesOfRed extends PrivateObjective {

    public ShadesOfRed(){
        super(  Settings.ObjectivesProperties.ShadesOfRed.getTitle(),
                Settings.ObjectivesProperties.ShadesOfRed.getDescription(),
                Settings.ObjectivesProperties.ShadesOfRed.getPath(),
                Settings.ObjectivesProperties.ShadesOfRed.getPoints(),
                Colour.RED);
    }

}
