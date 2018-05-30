package project.ing.soft.socket.request.connectionrequest;

public interface ConnectionRequestHandler {
    void handle(APConnectRequest request) throws Exception;
    void handle(APReconnectRequest request) throws Exception;
}
