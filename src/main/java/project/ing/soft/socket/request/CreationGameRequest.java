package project.ing.soft.socket.request;

public class CreationGameRequest extends AbstractRequest {
    private int numberOfPlayer;

    public CreationGameRequest(int numberOfPlayer) {
        this.numberOfPlayer = numberOfPlayer;
    }

    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
