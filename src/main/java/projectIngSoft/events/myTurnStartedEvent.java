package projectIngSoft.events;

import java.io.Serializable;

public class myTurnStartedEvent implements Event, Serializable {
    public myTurnStartedEvent() {
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
