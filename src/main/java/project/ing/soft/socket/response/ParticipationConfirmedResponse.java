package project.ing.soft.socket.response;

public class ParticipationConfirmedResponse implements IResponse {
    @Override
    public int getId() {
        return -1;
    }

    @Override
    public void accept(IResponseHandler handler) throws Exception{
        handler.handle(this);
    }
}