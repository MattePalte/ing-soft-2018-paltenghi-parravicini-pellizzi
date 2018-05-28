package project.ing.soft.socket.response.ConnectionResponse;

import project.ing.soft.socket.response.ConnectionResponse.ConnectionResponse;
import project.ing.soft.socket.response.ConnectionResponse.ConnectionResponseHandler;

import java.io.IOException;

public class ConnectionEstabilishedResponse implements ConnectionResponse {

    private String playerToken;

    public ConnectionEstabilishedResponse(String playerToken){
        this.playerToken = playerToken;
    }

    public String getToken(){
        return playerToken;
    }

    @Override
    public void accept(ConnectionResponseHandler handler) throws IOException {
        handler.handle(this);
    }
}
