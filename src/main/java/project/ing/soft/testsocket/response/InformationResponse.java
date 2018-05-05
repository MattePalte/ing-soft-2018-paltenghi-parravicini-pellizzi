package project.ing.soft.testsocket.response;

import project.ing.soft.Game;

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
    public int getId() {
        return -1;
    }

    @Override
    public void accept(IResponseHandler handler) throws Exception {
        handler.handle(this);
    }
}
