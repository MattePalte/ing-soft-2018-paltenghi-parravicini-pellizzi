package projectIngSoft.events;

import projectIngSoft.GameManager.IGameManager;

public class ModelChangedEvent implements Event {
    private IGameManager aGameCopy;

    public ModelChangedEvent(IGameManager aGameCopy) {
        this.aGameCopy = aGameCopy.clone();
    }

    public IGameManager getaGameCopy() {
        return aGameCopy;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
