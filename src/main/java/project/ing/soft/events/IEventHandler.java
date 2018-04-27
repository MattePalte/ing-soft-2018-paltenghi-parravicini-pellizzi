package project.ing.soft.events;

public interface IEventHandler {
    void respondTo(CurrentPlayerChangedEvent event);
    void respondTo(FinishedSetupEvent event);
    void respondTo(GameFinishedEvent event);
    void respondTo(PatternCardDistributedEvent event);
    void respondTo(MyTurnStartedEvent event);
    void respondTo(ModelChangedEvent event);
}
