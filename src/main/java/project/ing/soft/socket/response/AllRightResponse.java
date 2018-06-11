package project.ing.soft.socket.response;

public class AllRightResponse implements IResponse{
    private final int id;

    public AllRightResponse(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }


    @Override
    public void accept(IResponseHandler handler) {
        handler.handle(this);
    }
}
