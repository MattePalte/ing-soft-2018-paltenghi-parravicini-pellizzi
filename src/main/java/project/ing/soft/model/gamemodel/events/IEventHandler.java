package project.ing.soft.model.gamemodel.events;

public interface IEventHandler {

    void respondTo(CurrentPlayerChangedEvent    event);
    void respondTo(FinishedSetupEvent           event);
    void respondTo(GameFinishedEvent            event);
    void respondTo(PatternCardDistributedEvent  event);
    void respondTo(MyTurnStartedEvent           event);
    void respondTo(ModelChangedEvent            event);
    void respondTo(MyTurnEndedEvent             event);
    void respondTo(ToolcardActionRequestEvent   event);
    void respondTo(SetTokenEvent                event);
}
