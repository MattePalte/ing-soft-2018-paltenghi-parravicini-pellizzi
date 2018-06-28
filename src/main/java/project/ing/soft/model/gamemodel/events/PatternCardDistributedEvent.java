package project.ing.soft.model.gamemodel.events;

import project.ing.soft.model.cards.WindowPatternCard;
import project.ing.soft.model.cards.objectives.privates.PrivateObjective;

import java.io.Serializable;

public class PatternCardDistributedEvent implements Event, Serializable {
    // it means that pattern card have been distributed to the players
    // which have to decide which card to use and comunicate their decision
    // to the model through the controller

    private final WindowPatternCard one;
    private final WindowPatternCard two;
    private final PrivateObjective myPrivateObjective;

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
