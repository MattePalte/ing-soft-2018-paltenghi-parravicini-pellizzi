package projectIngSoft.events;

public interface IEventHandler {
    void respondTo(CurrentPlayerChangedEvent event);
    void respondTo(FinishedSetupEvent event);
    void respondTo(GameFinishedEvent event);
    void respondTo(PatternCardDistributedEvent event);
    void respondTo(myTurnStartedEvent event);
    void respondTo(ModelChangedEvent event);
}
