package project.ing.soft.socket.request.connectionrequest;

import project.ing.soft.exceptions.ActionNotPermittedException;
import project.ing.soft.exceptions.CodeInvalidException;
import project.ing.soft.exceptions.GameInvalidException;
import project.ing.soft.exceptions.NickNameAlreadyTakenException;

import java.io.IOException;

public interface ConnectionRequestHandler {
    void handle(APConnectRequest request) throws IOException, NickNameAlreadyTakenException, GameInvalidException;
    void handle(APReconnectRequest request) throws IOException, GameInvalidException, ActionNotPermittedException, CodeInvalidException;
}
