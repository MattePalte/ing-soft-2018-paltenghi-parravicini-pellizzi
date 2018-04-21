package projectIngSoft.events;

public interface Event {
    void accept(IEventHandler eventHandler);
}
