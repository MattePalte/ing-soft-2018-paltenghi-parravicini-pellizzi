package project.ing.soft.socket.response;

import project.ing.soft.model.gamemodel.events.Event;

public class EventResponse implements IResponse {
    private Event event;

    public EventResponse(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public void accept(IResponseHandler handler) {
        handler.handle(this);
    }
}
