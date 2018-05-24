package project.ing.soft.socket.request;

public class ReconnectionRequest implements ConnectionRequest {
    @Override
    public void accept(ClientConnectionRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
