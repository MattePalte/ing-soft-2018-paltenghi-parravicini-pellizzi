package project.ing.soft.model.gamemanager.events;

import project.ing.soft.model.gamemanager.IGameManager;

import java.io.Serializable;

public class ModelChangedEvent implements Event, Serializable {
    private IGameManager aGameCopy;

    public ModelChangedEvent(IGameManager aGameCopy) {
        //AVOID TO CREATE HERE THE COPY: WHICH TYPE IS IT??
        this.aGameCopy = aGameCopy;
    }

    public IGameManager getaGameCopy() {
        return aGameCopy;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
