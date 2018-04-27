package project.ing.soft.testsocket.response;

import java.io.Serializable;

public interface IResponse extends Serializable {
    void accept(IResponseHandler handler) throws Exception;
}
