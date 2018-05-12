package project.ing.soft.model.gamemanager.events;

public class TurnEndedEvent implements Event{
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
