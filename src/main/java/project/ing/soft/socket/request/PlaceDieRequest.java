package project.ing.soft.socket.request;

import project.ing.soft.model.Die;

public final class PlaceDieRequest extends AbstractRequest {
    public final Die aDie;
    public final String nickname;
    public final int colIndex;
    public final int rowIndex;

    public PlaceDieRequest( String nickname,Die aDie, int colIndex, int rowIndex) {
        this.aDie = aDie;
        this.nickname = nickname;
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
