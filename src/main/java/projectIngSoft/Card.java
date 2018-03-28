package projectIngSoft;

public abstract class Card {
    protected String title;
    protected String imgPath;

    public Card(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
}
