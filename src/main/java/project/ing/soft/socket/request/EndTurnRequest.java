package project.ing.soft.socket.request;

public class EndTurnRequest extends AbstractRequest {
    private String nickname;

    public EndTurnRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
