package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.Colour;

public class FluxBrush extends ToolCard {

    public FluxBrush() {
        super("Flux brush", "After drafting, \n" +
                "re-roll the drafted die.\nIf it cannot be placed\n return it to the Draft Pool",
                "toolcard/30%/toolcards-7.png", Colour.VIOLET, new FluxBrushFirstPart());
    }

    public FluxBrush(FluxBrush from){
        super(from);
    }

    public ToolCard copy(){
        return new FluxBrush(this);
    }

}
