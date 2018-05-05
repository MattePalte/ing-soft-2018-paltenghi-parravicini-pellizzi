package project.ing.soft.testsocket.request;

public class UpdateRequest extends AbstractRequest {
    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
