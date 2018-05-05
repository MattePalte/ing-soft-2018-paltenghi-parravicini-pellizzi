package project.ing.soft.testsocket.request;

import project.ing.soft.cards.toolcards.ToolCard;

public class PlayToolCardRequest extends AbstractRequest {
    private String nickname;
    private ToolCard aToolCard;
    public PlayToolCardRequest(String nickname, ToolCard aToolCard) {
        this.nickname = nickname;
        this.aToolCard = aToolCard;
    }

    public String getNickname() {
        return nickname;
    }

    public ToolCard getaToolCard() {
        return aToolCard;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
