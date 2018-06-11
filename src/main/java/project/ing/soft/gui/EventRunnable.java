package project.ing.soft.gui;

import project.ing.soft.model.gamemodel.events.Event;
import project.ing.soft.model.gamemodel.events.IEventHandler;

public class EventRunnable implements Runnable {
    private Event eventToExecute;
    private IEventHandler eventHandler;

    public EventRunnable(Event eventToExecute, IEventHandler anEventHandler) {
        this.eventHandler = anEventHandler;
        this.eventToExecute = eventToExecute;
    }

    @Override
    public void run() {
        eventToExecute.accept(eventHandler);
    }
}
