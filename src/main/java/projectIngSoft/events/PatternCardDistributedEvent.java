package projectIngSoft.events;

import projectIngSoft.View.IEventHandler;

public class PatternCardDistributedEvent implements Event{
    // it means that pattern card have been distributed to the players
    // which have to decide which card to use and comunicate their decision
    // to the model through the controller
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
