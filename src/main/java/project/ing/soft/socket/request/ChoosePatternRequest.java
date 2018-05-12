package project.ing.soft.socket.request;

import project.ing.soft.model.cards.WindowPatternCard;

public class ChoosePatternRequest extends AbstractRequest {
    private String nickname;
    private WindowPatternCard windowCard;
    private Boolean side;


    public ChoosePatternRequest(String nickname, WindowPatternCard windowCard, Boolean side) {

        this.nickname = nickname;
        this.windowCard = windowCard;
        this.side = side;
    }


    public String getNickname() {
        return nickname;
    }

    public WindowPatternCard getWindowCard() {
        return windowCard;
    }

    public Boolean getSide() {
        return side;
    }


    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
