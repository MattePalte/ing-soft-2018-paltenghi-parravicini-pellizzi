package project.ing.soft.events;

public class TurnEndedEvent implements Event{
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
