package project.ing.soft.socket.request;

import java.io.Serializable;

public interface ConnectionRequest extends Serializable {
    void accept(ClientConnectionRequestHandler handler) throws Exception;
}
