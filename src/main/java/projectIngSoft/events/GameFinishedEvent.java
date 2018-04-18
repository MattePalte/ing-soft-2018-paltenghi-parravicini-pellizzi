package projectIngSoft.events;

import projectIngSoft.View.IEventHandler;

public class GameFinishedEvent  implements Event {
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}

