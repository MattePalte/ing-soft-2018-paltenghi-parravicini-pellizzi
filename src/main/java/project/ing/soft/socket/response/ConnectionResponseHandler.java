package project.ing.soft.socket.response;

import project.ing.soft.socket.response.ConnectionEstabilishedResponse;
import project.ing.soft.socket.response.ConnectionRefusedResponse;

public interface ConnectionResponseHandler {
    void handle(ConnectionEstabilishedResponse response);
    void handle(ConnectionRefusedResponse response);
}
