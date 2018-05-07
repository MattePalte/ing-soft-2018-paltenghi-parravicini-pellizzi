package project.ing.soft.events;

import project.ing.soft.cards.WindowPatternCard;
import project.ing.soft.cards.objectives.privates.PrivateObjective;

import java.io.Serializable;

public class PatternCardDistributedEvent implements Event, Serializable {
    // it means that pattern card have been distributed to the players
    // which have to decide which card to use and comunicate their decision
    // to the model through the controller

    private WindowPatternCard one;
    private WindowPatternCard two;
    private PrivateObjective myPrivateObjective;

    public PatternCardDistributedEvent(PrivateObjective myPrivateObjective, WindowPatternCard one, WindowPatternCard two) {
        this.one = one;
        this.two = two;
        this.myPrivateObjective = myPrivateObjective;
    }

    public PrivateObjective getMyPrivateObjective(){
        return myPrivateObjective;
    }

    public WindowPatternCard getOne() {
        return one;
    }

    public WindowPatternCard getTwo() {
        return two;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
