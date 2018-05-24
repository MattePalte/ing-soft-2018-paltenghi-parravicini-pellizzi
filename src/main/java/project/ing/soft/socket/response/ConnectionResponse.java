package project.ing.soft.socket.response;

import java.io.Serializable;

public interface ConnectionResponse extends Serializable {
    void accept(ConnectionResponseHandler handler);
}
