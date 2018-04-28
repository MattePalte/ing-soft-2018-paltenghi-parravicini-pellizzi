package project.ing.soft.testsocket.response;

public class ExceptionalResponse implements IResponse{
    private Exception ex;

    public ExceptionalResponse(Exception ex) {
        this.ex = ex;
    }

    public Exception getEx() {
        return ex;
    }


    @Override
    public void accept(IResponseHandler handler) throws Exception {
        handler.handle(this);
    }
}
