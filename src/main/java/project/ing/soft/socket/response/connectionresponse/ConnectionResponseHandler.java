package project.ing.soft.socket.response.connectionresponse;

import java.io.IOException;

public interface ConnectionResponseHandler {
    void handle(ConnectionEstabilishedResponse response) throws IOException;
    void handle(ConnectionRefusedResponse response) throws Exception;

    void handle(NickNameAlreadyTakenResponse response) throws Exception;

}
