package projectIngSoft.testSocket.request;

import java.io.Serializable;

public interface IRequest extends Serializable {
    void accept(IRequestHandler handler) throws Exception;
}
