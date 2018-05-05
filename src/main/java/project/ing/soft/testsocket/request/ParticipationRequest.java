package project.ing.soft.testsocket.request;

import project.ing.soft.Game;

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
