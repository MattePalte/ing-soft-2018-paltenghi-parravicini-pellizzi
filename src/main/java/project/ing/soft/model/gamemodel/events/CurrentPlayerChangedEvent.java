package project.ing.soft.model.gamemodel.events;

import java.io.Serializable;

public class CurrentPlayerChangedEvent implements Event, Serializable {
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
