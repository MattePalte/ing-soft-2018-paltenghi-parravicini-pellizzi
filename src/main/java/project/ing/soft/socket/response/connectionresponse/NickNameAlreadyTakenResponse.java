package project.ing.soft.socket.response.ConnectionResponse;

import project.ing.soft.exceptions.NickNameAlreadyTakenException;

public class NickNameAlreadyTakenResponse implements ConnectionResponse {

    private NickNameAlreadyTakenException cause;

    public NickNameAlreadyTakenResponse(NickNameAlreadyTakenException cause){
        this.cause = cause;
    }

    public NickNameAlreadyTakenException getCause() {
        return cause;
    }

    @Override
    public void accept(ConnectionResponseHandler handler) {
        handler.handle(this);
    }
}
