package projectIngSoft.testSocket.response;

import projectIngSoft.testSocket.request.IRequestHandler;

import java.io.Serializable;

public interface IResponse extends Serializable {
    void accept(IResponseHandler handler) throws Exception;
}
