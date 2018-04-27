package project.ing.soft.testsocket.request;

import java.io.Serializable;

public interface IRequest extends Serializable {
    void accept(IRequestHandler handler) throws Exception;
}
