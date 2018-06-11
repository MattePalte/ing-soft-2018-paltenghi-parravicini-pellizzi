package project.ing.soft.socket.request;

public final class UpdateRequest extends AbstractRequest {
    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
