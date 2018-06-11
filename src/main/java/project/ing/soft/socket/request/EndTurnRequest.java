package project.ing.soft.socket.request;

public final class EndTurnRequest extends AbstractRequest {
    public final String nickname;

    public EndTurnRequest(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
