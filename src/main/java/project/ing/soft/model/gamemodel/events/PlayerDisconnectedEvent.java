package project.ing.soft.model.gamemodel.events;

public class PlayerDisconnectedEvent implements Event {

    private String nickname;

    public PlayerDisconnectedEvent(String nickname){
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
