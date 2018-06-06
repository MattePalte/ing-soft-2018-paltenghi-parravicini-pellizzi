package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.socket.APointSocket;

public class APConnectRequest implements ConnectionRequest {
    private String nickname;

    public APConnectRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void accept(APointSocket handler) throws Exception {
        handler.handle(this);
    }
}
