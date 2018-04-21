package projectIngSoft.events;

public class GameFinishedEvent  implements Event {
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}

