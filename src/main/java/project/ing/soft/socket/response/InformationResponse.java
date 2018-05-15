package project.ing.soft.socket.response;

import project.ing.soft.model.Game;

import java.util.List;

public class InformationResponse implements IResponse {
    private List<Game> gamesAvailable;
    public InformationResponse(List<Game> gamesAvailable){
        this.gamesAvailable = gamesAvailable;
    }

    public List<Game> getGamesAvailable() {
        return gamesAvailable;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public void accept(IResponseHandler handler) {
        handler.handle(this);
    }
}
