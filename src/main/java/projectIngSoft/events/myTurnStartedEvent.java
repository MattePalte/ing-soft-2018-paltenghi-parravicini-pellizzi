package projectIngSoft.events;

public class myTurnStartedEvent implements Event {
    public myTurnStartedEvent() {
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
