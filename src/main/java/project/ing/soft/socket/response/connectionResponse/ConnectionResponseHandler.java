package project.ing.soft.socket.response.connectionresponse;

public interface ConnectionResponseHandler {
    void handle(ConnectionEstabilishedResponse response);
    void handle(ConnectionRefusedResponse response);

    void handle(NickNameAlreadyTakenResponse response);
}
