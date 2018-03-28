package projectIngSoft.ToolCardsImpl;

import projectIngSoft.Colour;
import projectIngSoft.ToolCard;
import projectIngSoft.WindowFrame;

public class TaglierinaCircolare extends ToolCard {
    public TaglierinaCircolare() {
        super("Taglierina circolare", "Dopo aver scelto un dado,\n" +
                "scambia quel dado con un dado sul Tracciato dei Round", Colour.GREEN);
    }

    public void applyEffect(WindowFrame window) {

    }
}
