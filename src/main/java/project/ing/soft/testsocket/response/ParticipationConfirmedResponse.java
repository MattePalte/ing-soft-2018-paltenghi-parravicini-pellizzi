package project.ing.soft.testsocket.response;

public class ParticipationConfirmedResponse implements IResponse {
    @Override
    public void accept(IResponseHandler handler) throws Exception{
        handler.handle(this);
    }
}
