package project.ing.soft.socket.request.ConnectionRequest;

public interface ConnectionRequestHandler {
    void handle(JoinTheGameRequest request) throws Exception;
    void handle(ReconnectionRequest request) throws Exception;
}
