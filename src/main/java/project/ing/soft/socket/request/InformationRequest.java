package project.ing.soft.socket.request;

public final class InformationRequest extends AbstractRequest {
    public InformationRequest() {
        super();
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
