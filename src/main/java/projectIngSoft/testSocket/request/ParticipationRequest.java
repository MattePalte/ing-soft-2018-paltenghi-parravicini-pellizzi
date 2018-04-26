package projectIngSoft.testSocket.request;

import projectIngSoft.Game;

public class ParticipationRequest implements IRequest{
    private Game aGame;
    public ParticipationRequest(Game aGame) {
        super();
        this.aGame = aGame;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception{
        handler.handle(this);
    }
}
