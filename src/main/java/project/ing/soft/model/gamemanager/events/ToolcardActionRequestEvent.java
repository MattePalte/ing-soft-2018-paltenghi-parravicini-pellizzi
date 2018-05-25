package project.ing.soft.model.gamemanager.events;

import project.ing.soft.model.cards.toolcards.ToolCard;

public class ToolcardActionRequestEvent implements Event {
    private ToolCard card;
    public ToolcardActionRequestEvent(ToolCard card) {
        this.card = card;
    }

    public ToolCard getCard(){
        return card;
    }

    @Override
    public void accept(IEventHandler eventHandler) {
        eventHandler.respondTo(this);
    }
}
