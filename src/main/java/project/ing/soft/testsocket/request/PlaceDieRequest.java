package project.ing.soft.testsocket.request;

import project.ing.soft.Die;

public class PlaceDieRequest implements IRequest {
    private Die aDie;
    private String nickname;
    private int colIndex;
    private int rowIndex;

    public PlaceDieRequest( String nickname,Die aDie, int colIndex, int rowIndex) {
        this.aDie = aDie;
        this.nickname = nickname;
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
    }

    public Die getTheDie() {
        return aDie;
    }

    public String getNickname() {
        return nickname;
    }

    public int getColIndex() {
        return colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
