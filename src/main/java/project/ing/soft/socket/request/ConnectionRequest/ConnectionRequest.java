package project.ing.soft.socket.request.ConnectionRequest;

import java.io.Serializable;

public interface ConnectionRequest extends Serializable {
    void accept(ClientConnectionRequestHandler handler) throws Exception;
}
