package projectIngSoft.events;

public class CurrentPlayerChangedEvent implements Event {
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
