package project.ing.soft.socket.response.ConnectionResponse;

import project.ing.soft.socket.response.ConnectionResponse.ConnectionResponse;
import project.ing.soft.socket.response.ConnectionResponse.ConnectionResponseHandler;

public class ConnectionRefusedResponse implements ConnectionResponse {

    private Exception cause;

    public ConnectionRefusedResponse(Exception cause){
        this.cause = cause;
    }

    public Exception getCause(){
        return cause;
    }

    @Override
    public void accept(ConnectionResponseHandler handler) {
        handler.handle(this);
    }
}
