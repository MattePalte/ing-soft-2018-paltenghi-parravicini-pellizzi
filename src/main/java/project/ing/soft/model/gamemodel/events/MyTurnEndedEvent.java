package project.ing.soft.model.gamemodel.events;

public class MyTurnEndedEvent implements Event{
    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
