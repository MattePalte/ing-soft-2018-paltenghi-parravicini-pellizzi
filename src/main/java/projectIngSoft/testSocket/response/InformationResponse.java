package projectIngSoft.testSocket.response;

import projectIngSoft.Game;

import java.util.ArrayList;

public class InformationResponse implements IResponse {
    private ArrayList<Game> gamesAvailable;
    public InformationResponse(ArrayList<Game> gamesAvailable){
        this.gamesAvailable = gamesAvailable;
    }

    public ArrayList<Game> getGamesAvailable() {
        return gamesAvailable;
    }

    @Override
    public void accept(IResponseHandler handler) throws Exception {
        handler.handle(this);
    }
}
