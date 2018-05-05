package project.ing.soft.testsocket.request;

public class InformationRequest extends AbstractRequest {
    public InformationRequest() {
        super();
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
