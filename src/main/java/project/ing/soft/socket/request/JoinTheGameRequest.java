package project.ing.soft.socket.request;

public class JoinTheGameRequest implements ConnectionRequest {
    private String nickname;

    public JoinTheGameRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void accept(ClientConnectionRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
