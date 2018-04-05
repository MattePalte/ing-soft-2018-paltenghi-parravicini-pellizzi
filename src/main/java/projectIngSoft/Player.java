package projectIngSoft;

import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.WindowPattern;

public class Player {
    private final String     name;
    private WindowPattern    pattern;
    private PrivateObjective privateObjective;
    private Die[][]          placedDice;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPattern(WindowPattern pattern) {
        this.pattern = pattern;
    }

    public WindowPattern getPattern() {

        return pattern;
    }

    public void setPrivateObjective(PrivateObjective privateObjective) {
        this.privateObjective = privateObjective;
    }

    public PrivateObjective getPrivateObjective() {
        return privateObjective;
    }


    public Die[][] getPlacedDice(){
        return placedDice;
    }

}
