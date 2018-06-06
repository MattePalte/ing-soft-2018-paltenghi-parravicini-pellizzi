package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.socket.APointSocket;

public class APReconnectRequest implements ConnectionRequest {

    private String nickname;

    private String gameToken;

    public APReconnectRequest(String nickname, String gameToken){
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
    public void accept(APointSocket handler) throws Exception {
        handler.handle(this);
    }
}
