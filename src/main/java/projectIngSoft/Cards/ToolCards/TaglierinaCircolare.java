package projectIngSoft.Cards.ToolCards;

import projectIngSoft.Colour;
import projectIngSoft.Player;

public class TaglierinaCircolare extends ToolCard {
    public TaglierinaCircolare() {
        super("Taglierina circolare", "Dopo aver scelto un dado,\n" +
                "scambia quel dado con un dado sul Tracciato dei Round", Colour.GREEN);
    }

    @Override
    public void applyEffect(Player p) {

    }
}
