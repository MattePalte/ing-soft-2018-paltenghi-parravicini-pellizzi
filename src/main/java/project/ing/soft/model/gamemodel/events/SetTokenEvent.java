package project.ing.soft.model.gamemodel.events;

public class SetTokenEvent implements Event {
    private String token;

    public SetTokenEvent(String token) {
        this.token = token;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }

    public String getToken(){
        return token;
    }
}
