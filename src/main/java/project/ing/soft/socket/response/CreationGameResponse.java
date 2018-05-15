package project.ing.soft.socket.response;

public class CreationGameResponse implements IResponse {
    @Override
    public int getId() {
        return -1;
    }

    @Override
    public void accept(IResponseHandler handler)  {
        handler.handle(this);
    }
}
