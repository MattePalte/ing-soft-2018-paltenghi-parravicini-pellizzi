package project.ing.soft.events;

public interface Event {
    void accept(IEventHandler eventHandler);
}
