package project.ing.soft.socket.request;

public class JoinTheGameRequest extends AbstractRequest {
    private String nickname;
    public JoinTheGameRequest(String nickname) {
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
