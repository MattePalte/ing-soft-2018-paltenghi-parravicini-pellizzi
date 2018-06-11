package project.ing.soft.model.gamemodel.events;

import java.io.Serializable;

public class FinishedSetupEvent implements Event, Serializable {
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
