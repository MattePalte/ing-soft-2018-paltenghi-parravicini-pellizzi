package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.socket.APointSocket;

import java.io.IOException;

public final class APReconnectRequest implements ConnectionRequest {

    public final String nickname;

    public final String gameToken;

    public APReconnectRequest(String nickname, String gameToken){
        this.nickname = nickname;
        this.gameToken = gameToken;
    }


    @Override
    public void accept(APointSocket handler) throws CodeInvalidException, GameInvalidException, ActionNotPermittedException, IOException {
        handler.handle(this);
    }
}
