package projectIngSoft.events;

import projectIngSoft.View.IEventHandler;

public class CurrentPlayerChangedEvent implements Event {
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
