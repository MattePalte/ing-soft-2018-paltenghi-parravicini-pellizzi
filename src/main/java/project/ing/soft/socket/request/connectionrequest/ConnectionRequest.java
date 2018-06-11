package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;
import project.ing.soft.socket.APointSocket;

import java.io.IOException;
import java.io.Serializable;

/**
 * The Connection request can be thought as a disjoined set of request,
 * that act analogously to {@link project.ing.soft.socket.request.AbstractRequest}
 * but it's used only during the initial phase of the game.
 */
public interface ConnectionRequest extends Serializable {
    void accept(APointSocket handler) throws NickNameAlreadyTakenException, GameInvalidException, IOException, CodeInvalidException, ActionNotPermittedException;
}
