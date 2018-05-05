package project.ing.soft.testsocket.response;

public class ExceptionalResponse implements IResponse{
    private final int id;
    private Exception ex;

    public ExceptionalResponse(Exception ex, int id) {
        this.id = id;
        this.ex = ex;
    }

    public Exception getEx() {
        return ex;
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
