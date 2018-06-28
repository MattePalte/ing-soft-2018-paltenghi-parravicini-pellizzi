package project.ing.soft.model.cards.objectives.privates;

import project.ing.soft.Settings;
import project.ing.soft.model.Colour;
import project.ing.soft.model.Player;
import project.ing.soft.model.StringBoxBuilder;

import java.io.Serializable;

public class RearPrivateObjective extends PrivateObjective implements Serializable {
    public RearPrivateObjective() {
        super("SAGRADA", "Rear of a card", "", 0,Colour.WHITE);
    }

    @Override
    public int checkCondition(Player window) {
        return 0;
    }
    @Override
    public String toString(){
        StringBoxBuilder aBuilder = new StringBoxBuilder(new StringBoxBuilder.DOUBLELINESQUAREANGLE(),Settings.instance().getTextCardWidth(), Settings.instance().getTextCardHeight());
        aBuilder.appendToTop(this.getTitle());
        aBuilder.prependToBottom(this.getTitle());
        return aBuilder.toString();
    }


}
