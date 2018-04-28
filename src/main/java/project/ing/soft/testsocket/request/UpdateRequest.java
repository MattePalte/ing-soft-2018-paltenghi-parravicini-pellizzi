package project.ing.soft.testsocket.request;

public class UpdateRequest implements IRequest {
    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
