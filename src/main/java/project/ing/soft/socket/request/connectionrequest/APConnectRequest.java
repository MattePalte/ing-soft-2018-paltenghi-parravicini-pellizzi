package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.APointSocket;

import java.io.IOException;

public final class APConnectRequest implements ConnectionRequest {
    public final String nickname;

    public APConnectRequest(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void accept(APointSocket handler) throws NickNameAlreadyTakenException, GameInvalidException, IOException {
        handler.handle(this);
    }
}
