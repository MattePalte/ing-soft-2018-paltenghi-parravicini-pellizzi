package project.ing.soft.model.cards.toolcards;

import project.ing.soft.model.Colour;


public class FluxRemover extends ToolCard {


    public FluxRemover() {
        super("Flux Remover", "After drafting, return the die to the Dice Bag and pull a die from the bag." +
                        "Choose a value and place the new die\n" +
                        "obeying all placement restrictions or return it to the Draft Pool",
                "toolcard/30%/toolcards-12.png",  Colour.VIOLET, new FluxRemoverFirstPart());
    }

    public FluxRemover(FluxRemover from) {
        super(from);
    }

    @Override
    public ToolCard copy() {
        return new FluxRemover(this);
    }
}

