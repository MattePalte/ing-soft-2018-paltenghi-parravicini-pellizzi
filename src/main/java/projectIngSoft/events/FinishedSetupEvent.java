package projectIngSoft.events;

public class FinishedSetupEvent implements Event {
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
