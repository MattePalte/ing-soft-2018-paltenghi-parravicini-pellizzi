package project.ing.soft.model.gamemodel.events;

/**
 * Interface to handle events delivered by the GameModel object (on the server)
 * It is used according to visitor pattern rules.
 * There is an overloading on toRespond method, each of them takes a specific event
 * containing specific relevant information.
 * For more details on the content of each event type please refer to event hierarchy.
 */
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
    void respondTo(PlayerReconnectedEvent       event);
    void respondTo(PlayerDisconnectedEvent      event);
}
