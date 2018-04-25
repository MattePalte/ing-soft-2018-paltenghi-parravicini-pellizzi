package projectIngSoft.events;

import projectIngSoft.GameManager.IGameManager;

public class ModelChangedEvent implements Event {
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
