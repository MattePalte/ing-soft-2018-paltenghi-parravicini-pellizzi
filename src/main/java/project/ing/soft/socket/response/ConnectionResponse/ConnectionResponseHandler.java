package project.ing.soft.socket.response.ConnectionResponse;

public interface ConnectionResponseHandler {
    void handle(ConnectionEstabilishedResponse response);
    void handle(ConnectionRefusedResponse response);

    void handle(NickNameAlreadyTakenResponse response);
}
