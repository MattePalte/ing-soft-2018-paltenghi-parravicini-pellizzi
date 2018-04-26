package projectIngSoft.testSocket.request;

public class InformationRequest implements  IRequest{
    public InformationRequest() {
        super();
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
