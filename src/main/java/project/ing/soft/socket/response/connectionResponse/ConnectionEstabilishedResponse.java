package project.ing.soft.socket.response.connectionresponse;

public class ConnectionEstabilishedResponse implements ConnectionResponse {

    private String playerToken;

    public ConnectionEstabilishedResponse(String playerToken){
        this.playerToken = playerToken;
    }

    public String getToken(){
        return playerToken;
    }

    @Override
    public void accept(ConnectionResponseHandler handler) {
        handler.handle(this);
    }
}
