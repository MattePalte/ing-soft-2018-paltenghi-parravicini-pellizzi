package projectIngSoft;

public abstract class ObjectiveCard extends Card{

    private int points;

    public ObjectiveCard(String title, String description, int points){
        super(title, description);
        this.points = points;
    }



    public int getPoints(){
        return points;
    }

    public int countPoints(WindowFrame window){
        return getPoints()*checkCondition(window);
    }

    public abstract int checkCondition(WindowFrame window);

}
