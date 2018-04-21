package projectIngSoft.events;

import projectIngSoft.Cards.WindowPatternCard;

public class PatternCardDistributedEvent implements Event{
    // it means that pattern card have been distributed to the players
    // which have to decide which card to use and comunicate their decision
    // to the model through the controller

    private WindowPatternCard one;
    private WindowPatternCard two;

    public PatternCardDistributedEvent(WindowPatternCard one, WindowPatternCard two) {
        this.one = one;
        this.two = two;
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
