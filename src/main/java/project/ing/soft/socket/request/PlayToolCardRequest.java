package project.ing.soft.socket.request;

import project.ing.soft.model.cards.toolcards.ToolCard;

public final class PlayToolCardRequest extends AbstractRequest {
    public final String nickname;
    public final ToolCard aToolCard;
    public PlayToolCardRequest(String nickname, ToolCard aToolCard) {
        this.nickname = nickname;
        this.aToolCard = aToolCard;
    }


    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
