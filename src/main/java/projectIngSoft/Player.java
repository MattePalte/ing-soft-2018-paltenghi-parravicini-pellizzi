package projectIngSoft;

import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;
import projectIngSoft.Cards.WindowPattern;

public class Player {
    private final String myName;
    private WindowPattern myPattern;
    private PrivateObjective myPrivateObjective;

    public Player(String name) {
        this.myName = name;
    }

    public String getName() {
        return myName;
    }

    public void setMyPattern(WindowPattern myPattern) {
        this.myPattern = myPattern;
    }

    public WindowPattern getMyPattern() {

        return myPattern;
    }

    public PrivateObjective getMyPrivateObjective() {
        return myPrivateObjective;
    }

    public void setMyPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;
    }

}
