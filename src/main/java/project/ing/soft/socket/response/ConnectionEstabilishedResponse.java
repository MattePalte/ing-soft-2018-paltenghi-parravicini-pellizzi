package project.ing.soft.socket.response;

public class ConnectionEstabilishedResponse implements ConnectionResponse {

    @Override
    public void accept(ConnectionResponseHandler handler) {
        handler.handle(this);
    }
}
