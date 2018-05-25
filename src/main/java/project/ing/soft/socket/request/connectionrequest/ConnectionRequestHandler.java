package project.ing.soft.socket.request.connectionrequest;

public interface ConnectionRequestHandler {
    void handle(JoinTheGameRequest request) throws Exception;
    void handle(ReconnectionRequest request) throws Exception;
}
