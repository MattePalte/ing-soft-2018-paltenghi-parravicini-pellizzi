package project.ing.soft.socket.request.connectionrequest;

public class ReconnectionRequest implements ConnectionRequest {

    private String nickname;

    private String gameToken;

    public ReconnectionRequest(String nickname, String gameToken){
        this.nickname = nickname;
        this.gameToken = gameToken;
    }

    public String getNickname(){
        return nickname;
    }

    public String getGameToken(){
        return gameToken;
    }

    @Override
    public void accept(ClientConnectionRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
