package project.ing.soft.socket.request;

import project.ing.soft.model.Game;

public class ParticipationRequest extends AbstractRequest {
    private Game aGame;
    public ParticipationRequest(Game aGame) {
        super();
        this.aGame = aGame;
    }

    public Game getaGame() {
        return aGame;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception{
        handler.handle(this);
    }
}
