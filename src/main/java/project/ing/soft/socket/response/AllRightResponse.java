package project.ing.soft.socket.response;

public class AllRightResponse implements IResponse{
    int id;

    public AllRightResponse(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }


    @Override
    public void accept(IResponseHandler handler) throws Exception {
        handler.handle(this);
    }
}
