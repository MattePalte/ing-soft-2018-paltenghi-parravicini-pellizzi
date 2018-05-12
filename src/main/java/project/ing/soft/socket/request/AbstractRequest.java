package project.ing.soft.socket.request;
import java.io.Serializable;

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
