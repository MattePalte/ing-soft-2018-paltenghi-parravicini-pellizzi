package project.ing.soft.socket.request.ConnectionRequest;

public class ReconnectionRequest implements ConnectionRequest {

    private String nickname;

    private String gameToken;

    public ReconnectionRequest(String nickname, String gameToken){
        this.nickname = nickname;
        this.gameToken = gameToken;
    }

    @Override
    public void accept(ClientConnectionRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
