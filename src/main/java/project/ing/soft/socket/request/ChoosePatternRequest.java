package project.ing.soft.socket.request;

import project.ing.soft.model.cards.WindowPatternCard;

public final class ChoosePatternRequest extends AbstractRequest {
    public final String nickname;
    public final WindowPatternCard windowCard;
    public final Boolean frontSide;


    public ChoosePatternRequest(String nickname, WindowPatternCard windowCard, Boolean frontSide) {

        this.nickname = nickname;
        this.windowCard = windowCard;
        this.frontSide = frontSide;
    }


    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
