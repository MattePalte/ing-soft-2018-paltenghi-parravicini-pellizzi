package project.ing.soft.socket.response.connectionresponse;

import java.io.IOException;

public class ConnectionEstabilishedResponse implements ConnectionResponse {

    public ConnectionEstabilishedResponse() {
    }

    @Override
    public void accept(ConnectionResponseHandler handler) throws IOException {
        handler.handle(this);
    }
}
