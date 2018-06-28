package project.ing.soft.model.gamemodel.events;

public class PlayerReconnectedEvent implements Event {

    private final String nickname;

    public PlayerReconnectedEvent(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return nickname;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
