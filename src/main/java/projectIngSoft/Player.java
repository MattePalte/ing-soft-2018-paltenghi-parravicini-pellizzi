package projectIngSoft;

import projectIngSoft.Cards.Objectives.Privates.PrivateObjective;

public class Player {
    private final String myName;
    private WindowFrame myFrame;
    private PrivateObjective myPrivateObjective;

    public Player(String name) {
        this.myName = name;
    }

    public String getName() {
        return myName;
    }

    public WindowFrame getFrame() {
        return myFrame;
    }

    public void setFrame(WindowFrame frame) {
        this.myFrame = frame;
    }

    public PrivateObjective getMyPrivateObjective() {
        return myPrivateObjective;
    }

    public void setMyPrivateObjective(PrivateObjective myPrivateObjective) {
        this.myPrivateObjective = myPrivateObjective;
    }

}
