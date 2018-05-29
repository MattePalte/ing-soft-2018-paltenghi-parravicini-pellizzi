package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.accesspoint.APointSocket;

import java.io.Serializable;

public interface ConnectionRequest extends Serializable {
    void accept(APointSocket handler) throws Exception;
}
