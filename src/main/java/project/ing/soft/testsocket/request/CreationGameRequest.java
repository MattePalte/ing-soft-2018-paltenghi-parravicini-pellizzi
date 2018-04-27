package project.ing.soft.testsocket.request;

public class CreationGameRequest implements IRequest {
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
