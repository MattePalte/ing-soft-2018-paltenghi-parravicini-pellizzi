package projectIngSoft.events;

import projectIngSoft.View.IEventHandler;

public interface Event {
    void accept(IEventHandler eventHandler);
}
