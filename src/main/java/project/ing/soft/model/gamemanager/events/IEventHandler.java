package project.ing.soft.model.gamemanager.events;

public interface IEventHandler {
    void respondTo(PlaceThisDieEvent event);

    void respondTo(CurrentPlayerChangedEvent event);
    void respondTo(FinishedSetupEvent event);
    void respondTo(GameFinishedEvent event);
    void respondTo(PatternCardDistributedEvent event);
    void respondTo(MyTurnStartedEvent event);
    void respondTo(ModelChangedEvent event);
    void respondTo(MyTurnEndedEvent event);
}
