package projectIngSoft;

public abstract class ObjectiveCard extends Card{
    protected String description;
    protected int points;

    public ObjectiveCard(String title, String description, int points){
        super(title, description);
        this.points = points;
    }

    public String getDescription(){
        return description;
    }

    public abstract void countPoints();

    public abstract int checkCondition(WindowFrame window);

}
