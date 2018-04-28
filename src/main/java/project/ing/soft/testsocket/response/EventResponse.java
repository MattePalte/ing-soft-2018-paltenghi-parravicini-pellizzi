package project.ing.soft.testsocket.response;

import project.ing.soft.events.Event;

public class EventResponse implements IResponse {
    private Event event;

    public EventResponse(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public void accept(IResponseHandler handler) throws Exception {
        handler.handle(this);
    }
}
