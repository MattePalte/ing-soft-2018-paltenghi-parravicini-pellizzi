package projectIngSoft.View;

import projectIngSoft.events.CurrentPlayerChangedEvent;
import projectIngSoft.events.FinishedSetupEvent;
import projectIngSoft.events.GameFinishedEvent;
import projectIngSoft.events.PatternCardDistributedEvent;

public interface IEventHandler {
    void respondTo(CurrentPlayerChangedEvent event);
    void respondTo(FinishedSetupEvent event);
    void respondTo(GameFinishedEvent event);
    void respondTo(PatternCardDistributedEvent event);
}
