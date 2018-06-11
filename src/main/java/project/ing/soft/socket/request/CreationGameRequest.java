package project.ing.soft.socket.request;

public final class CreationGameRequest extends AbstractRequest {
    public final int numberOfPlayer;

    public CreationGameRequest(int numberOfPlayer) {
        this.numberOfPlayer = numberOfPlayer;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
