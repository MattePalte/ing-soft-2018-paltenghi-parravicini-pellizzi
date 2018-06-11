package project.ing.soft.socket.request;
import java.io.Serializable;

/**
 * In collaboration with {@link project.ing.soft.socket.response.IResponse} this class
 * allow communication between socket endpoint.
 * Althought the only method recalled ({@link #accept(IRequestHandler)}) enable the visitor {@link IRequestHandler}
 * to take place this trasmission fashion can recall the Command design pattern.
 * This class has been thought to define the essential information an AbstractRequest can
 * carry and its usage in {@link project.ing.soft.socket.ViewProxyOverSocket}
 */

public abstract class AbstractRequest implements Serializable {
    private int id;
    private boolean hasBeenHandled;
    private Exception exception;

    public int         getId(){
        return id;
    }
    public void        setId(int id){
        this.id = id;
    }

    public boolean     beenHandled(){
        return hasBeenHandled;
    }
    public void        setBeenHandled(Boolean flag){
        this.hasBeenHandled = flag;
    }

    public boolean     hasException(){
        return this.exception != null;
    }
    public Exception   getException(){
        return exception;
    }
    public void        setException(Exception ex){
        this.exception = ex;
    }

    public abstract void accept(IRequestHandler handler) throws Exception;
}
