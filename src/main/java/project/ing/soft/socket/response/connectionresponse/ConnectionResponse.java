package project.ing.soft.socket.response.connectionresponse;

import java.io.Serializable;

public interface ConnectionResponse extends Serializable {
    void accept(ConnectionResponseHandler handler) throws Exception;
}
