package project.ing.soft.model.gamemodel.events;

import project.ing.soft.model.gamemodel.IGameModel;

import java.io.Serializable;

public class ModelChangedEvent implements Event, Serializable {
    private IGameModel aGameCopy;

    public ModelChangedEvent(IGameModel aGameCopy) {
        //AVOID TO CREATE HERE THE COPY: WHICH TYPE IS IT??
        this.aGameCopy = aGameCopy;
    }

    public IGameModel getaGameCopy() {
        return aGameCopy;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
